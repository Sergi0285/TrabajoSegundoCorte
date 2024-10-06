let token = localStorage.getItem('token');

window.onload = function() {
    verificarTokenYRedireccionarALogin();
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

function subirVideo(){
    var title = $('#title').val();
    var description = $('#description').val();
    var file = $('#file')[0].files[0];

    // Validar que todos los campos están completos
    if (!title || !description || !file) {
        alert("Please fill all the fields and select a video file.");
        return;
    }

    // Crear objeto FormData para subir el archivo
    var formData = new FormData();
    formData.append("titulo", title);
    formData.append("descripcion", description);
    formData.append("file", file);
    formData.append("alias", username);

    // Enviar datos al servidor (AJAX)
    $.ajax({
        url: '/videos/upload',  // Cambia la URL al endpoint adecuado
        type: 'POST',
        headers: {
            'Authorization': 'Bearer ' + token
        },
        data: formData,
        processData: false,
        contentType: false,
        success: function(response) {
            alert("Video uploaded successfully!");
            // Actualizar la lista de videos
            loadVideos();
        },
        error: function(xhr, status, error) {
            alert("Error uploading video: " + error);
        }
    });
}
