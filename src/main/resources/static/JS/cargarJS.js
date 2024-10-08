let token = localStorage.getItem('token');

window.onload = function() {
    verificarTokenYRedireccionarALogin();
    categorias();
    $('#video').on('change', function() {
        // Obtener el nombre del archivo
        const fileName = $(this).val().split('\\').pop(); // Para obtener solo el nombre del archivo
        // Actualizar el label con el nombre del archivo
        $(this).next('.custom-file-label').html(fileName);
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
    var file = $('#video')[0].files[0];
    
    // Obtener categorías seleccionadas
    const selectedCategories = $('input[type="checkbox"]:checked').map(function() {
        return this.value; // Obtener el valor (nombre de la categoría)
    }).get();

    // Validar que todos los campos están completos
    if (!title || !description || !file || selectedCategories.length === 0) {
        alert("Por favor, completa todos los campos y selecciona al menos una categoría.");
        return;
    }

    // Crear objeto FormData para subir el archivo
    var formData = new FormData();
    formData.append("titulo", title);
    formData.append("descripcion", description);
    formData.append("file", file);
    formData.append("alias", username); // Asegúrate de que `username` esté definido

    // Subir el video
    $.ajax({
        url: '/videos/upload',  // Cambia la URL al endpoint adecuado
        type: 'POST',
        headers: {
            'Authorization': 'Bearer ' + token // Asegúrate de que `token` esté definido
        },
        data: formData,
        processData: false,
        contentType: false,
        success: function(videoId) {
            alert("Video subido correctamente! ID: " + videoId);
            console.log(videoId);
            $.ajax({
                    url: '/categoria/add',  // Cambia la URL al endpoint adecuado
                    type: 'POST',
                    headers: {
                        'Authorization': 'Bearer ' + token // Asegúrate de que `token` esté definido
                    },
                    data: {
                        Id: videoId, // Asegúrate de que videoId tenga un valor válido
                        categorias: selectedCategories // Asegúrate de que sea un array de categorías
                    },
                    success: function(response) {
                        alert("Categorías agregadas correctamente.");
                        // Aquí puedes actualizar la UI si es necesario
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
