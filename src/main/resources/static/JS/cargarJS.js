let token = localStorage.getItem('token');

window.onload = function() {
    verificarTokenYRedireccionarALogin();
    categorias();
    $('#video').on('change', function() {
        const file = this.files[0];
        if (file) {
            const fileType = file.type;
            const allowedTypes = ['video/mp4', 'video/mpeg', 'video/ogg', 'video/webm']; // Tipos permitidos para videos

            // Validar el tipo de archivo
            if (!allowedTypes.includes(fileType)) {
                alert("Por favor, selecciona un archivo de video válido (mp4, mpeg, ogg, webm).");
                $(this).val(''); // Limpiar el input
                $(this).next('.custom-file-label').html("Elige un archivo"); // Resetear el label
                return;
            }
            // Obtener el nombre del archivo
            const fileName = $(this).val().split('\\').pop();
            $(this).next('.custom-file-label').html(fileName);
        }
    });

    $('#image').on('change', function() {
        const file = this.files[0];
        if (file) {
            const fileType = file.type;
            const allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/bmp']; // Tipos permitidos para imágenes

            // Validar el tipo de archivo
            if (!allowedTypes.includes(fileType)) {
                alert("Por favor, selecciona un archivo de imagen válido (jpeg, png, gif, bmp).");
                $(this).val(''); // Limpiar el input
                $(this).next('.custom-file-label').html("Elige un archivo"); // Resetear el label
                return;
            }
            // Obtener el nombre del archivo
            const fileName = $(this).val().split('\\').pop();
            $(this).next('.custom-file-label').html(fileName);
        }
    });
}

function verificarTokenYRedireccionarALogin() {
    // Verificar si el token está presente
    if (token === null) {
        window.location.href = '/Vistas/inicioVista.html';
    }
}

var tokenParts = token.split('.');
var tokenPayload = JSON.parse(atob(tokenParts[1]));
var username = tokenPayload.sub;

function subirVideo() {
    // Obtener valores del formulario
    var title = $('#title').val();
    var description = $('#description').val();
    var videoFile = $('#video')[0].files[0];
    var imageFile = $('#image')[0].files[0] || null; // Tomar la imagen si existe
    
    // Obtener categorías seleccionadas
    const selectedCategories = $('input[type="checkbox"]:checked').map(function() {
        return this.value;
    }).get();

    // Validar que todos los campos están completos
    if (!title || !description || !videoFile || selectedCategories.length === 0) {
        alert("Por favor, completa todos los campos y selecciona al menos una categoría.");
        return;
    }

    // Crear objeto FormData para subir el archivo
    var formData = new FormData();
    formData.append("titulo", title);
    formData.append("descripcion", description);
    formData.append("file", videoFile); // Subir el video
    formData.append("alias", username); 
    formData.append("image", imageFile);

    // Subir el video
    $.ajax({
        url: '/videos/upload',
        type: 'POST',
        headers: {
            'Authorization': 'Bearer ' + token
        },
        data: formData,
        processData: false,
        contentType: false,
        success: function(videoId) {
            alert("Video subido correctamente! ID: " + videoId);
            console.log(videoId);
            $.ajax({
                url: '/categoria/add',
                type: 'POST',
                headers: {
                    'Authorization': 'Bearer ' + token
                },
                data: {
                    Id: videoId,
                    categorias: selectedCategories
                },
                success: function(response) {
                    alert("Categorías agregadas correctamente.");
                },
                error: function(xhr, status, error) {
                    alert("Error al agregar categorías: " + error);
                }
            });
        },
        error: function(xhr, status, error) {
            alert("Error al subir el video: " + error);
        }
    });
}

function categorias() {
        $.ajax({
            url: '/videos/categorias', // Cambia la URL al endpoint adecuado
            type: 'GET',
            headers: {
                'Authorization': 'Bearer ' + token // Asegúrate de que 'token' esté definido
            },
            success: function(response) {
                const categoriesContainer = $('#categoriesCheckboxes');
                categoriesContainer.empty();
                response.forEach(function(category) {
                    const checkbox = `
                        <div class="checkbox-label">
                            <input type="checkbox" value="${category}" id="${category}">
                            <label for="${category}">${category}</label>
                        </div>`;
                    categoriesContainer.append(checkbox);
                });
            },
            error: function(xhr, status, error) {
                alert("Error al cargar categorías: " + error);
            }
        });
}
