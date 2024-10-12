let token = localStorage.getItem('token');

var tokenParts = token.split('.');
var tokenPayload = JSON.parse(atob(tokenParts[1]));
var username = tokenPayload.sub;


$(document).ready(function() {
    verificarTokenYRedireccionarALogin();
    loadVideos();
    
    $('#submit-comment').on('click', function() {
        const videoId = $(this).data('video-id');
        const comentario = $('#comment-input').val();
        
        if (comentario) {
            agregarComentario(videoId, comentario);
            $('#comment-input').val(''); // Limpiar el campo de texto
        }
    });
});

function verificarTokenYRedireccionarALogin() {
    if (token === null) {
        window.location.href = '/Vistas/inicioVista.html';
    }
}

function loadVideos() {
    $.ajax({
        url: '/videos/Lista',
        type: 'GET',
        headers: {
            'Authorization': 'Bearer ' + token
        },
        success: function(videos) {
            videos.forEach(video => {
                const videoElement = $(`
                    <div class="video-item">
                        <h3>${video.titulo}</h3>
                        <p>${video.descripcion}</p>
                        <button class="play-button" data-id="${video.idVideo}">Reproducir</button>
                    </div>
                `);
                $('#videos-container').append(videoElement);
            });

            // Asignar evento click a cada botón de reproducción
            $('.play-button').on('click', function() {
                const currentVideoId = $(this).data('id');
                playVideo(currentVideoId);
            });
        },
        error: function(error) {
            console.error('Error al cargar los videos:', error);
        }
    });
}

function playVideo(identificador) {
    $.ajax({
        url: '/videos/ver',
        type: 'GET',
        headers: {
            'Authorization': 'Bearer ' + token
        },
        data: { id: identificador },
        xhr: function() {
            const xhr = new window.XMLHttpRequest();
            xhr.responseType = 'blob';
            return xhr;
        },
        success: function(videoBlob) {
            const videoObjectURL = URL.createObjectURL(videoBlob);
            $('#video-source').attr('src', videoObjectURL);
            $('#video-player')[0].load();
            $('#video-player')[0].play();
            $('#submit-comment').data('video-id', identificador); // Guardar el ID del video actual
            loadComments(identificador); // Cargar comentarios del video
        },
        error: function(error) {
            console.error('Error al cargar el video:', error);
        }
    });
}

function loadComments(videoId) {
    $.ajax({
        url: `/comentarios/video/${videoId}`,
        type: 'GET',
        headers: {
            'Authorization': 'Bearer ' + token
        },
        success: function(comentarios) {
            $('#comments-list').empty();
            comentarios.forEach(comentario => {
                const comentarioElement = $(`
                    <div class="comment-item">
                        <p><strong>${comentario.usuario.username}</strong>: ${comentario.comentario} <em>${comentario.fechaComentario}</em></p>
                    </div>
                `);
                $('#comments-list').append(comentarioElement);
            });
        },
        error: function(error) {
            console.error('Error al cargar los comentarios:', error);
        }
    });
}

function agregarComentario(videoId, comentario) {
    console.log(videoId);
    console.log(comentario);
    $.ajax({
        url: "/comentarios/agregar",
        type: 'POST',
        headers: {
            'Authorization': 'Bearer ' + token,
            'Content-Type': 'application/json'
        },
        data: JSON.stringify({
            comentario: comentario,
            videoId: videoId,
            username: username
        }),
        success: function(response) {
            console.log(response);
            loadComments(videoId); // Volver a cargar los comentarios después de agregar
        },
        error: function(error) {
            console.error('Error al agregar el comentario:', error);
        }
    });
}