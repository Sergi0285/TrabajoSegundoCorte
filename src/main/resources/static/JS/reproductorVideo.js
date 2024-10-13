let token = localStorage.getItem('token');

var tokenParts = token.split('.');
var tokenPayload = JSON.parse(atob(tokenParts[1]));
var username = tokenPayload.sub;

console.log(username);

let currentEditingCommentId = null; // Variable para guardar el ID del comentario que se está editando

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

    $('#save-edit-comment').on('click', function() {
        const newComment = $('#edit-comment-input').val();
        if (currentEditingCommentId && newComment) {
            editarComentario(currentEditingCommentId, newComment);
            $('#editCommentModal').hide(); // Ocultar el modal
            $('#edit-comment-input').val(''); // Limpiar el campo de texto del modal
        }
    });

    $('#close-modal').on('click', function() {
        $('#editCommentModal').hide(); // Ocultar el modal
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
                const isOwner = comentario.usuario.username === username;

                // Construir el elemento de comentario
                const comentarioElement = $(`
                    <div class="comment-item">
                        <p>
                            <span class="comment-author">${comentario.usuario.username}</span>: 
                            ${comentario.comentario} 
                            <em class="comment-date">${comentario.fechaComentario}</em>
                        </p>
                        ${isOwner ? `
                        <div class="options-menu">⋮
                            <div class="options-menu-content">
                                <a href="#" class="edit-comment" data-id="${comentario.idComentario}" data-comment="${comentario.comentario}">Editar</a>
                                <a href="#" class="delete-comment" data-id="${comentario.idComentario}">Eliminar</a>
                            </div>
                        </div>` : ''}
                    </div>
                `);

                // Solo agregar eventos si el usuario es el propietario
                if (isOwner) {
                    comentarioElement.find('.options-menu').on('click', function() {
                        $(this).find('.options-menu-content').toggle();
                    });

                    // Manejo de la edición de comentarios
                    comentarioElement.find('.edit-comment').on('click', function(e) {
                        e.preventDefault();
                        currentEditingCommentId = $(this).data('id');
                        const currentCommentText = $(this).data('comment');
                        $('#edit-comment-input').val(currentCommentText);
                        $('#editCommentModal').show();
                    });

                    // Manejo de la eliminación de comentarios
                    comentarioElement.find('.delete-comment').on('click', function(e) {
                        e.preventDefault();
                        const comentarioId = $(this).data('id');
                        eliminarComentario(comentarioId, videoId);
                    });
                }

                $('#comments-list').append(comentarioElement);
            });
        },
        error: function(error) {
            console.error('Error al cargar los comentarios:', error);
        }
    });
}


function editarComentario(comentarioId, nuevoComentario) {
    $.ajax({
        url: `/comentarios/editar/${comentarioId}`, // Cambia esta URL según tu API
        type: 'PUT', // Usar PUT para editar
        headers: {
            'Authorization': 'Bearer ' + token,
            'Content-Type': 'application/json'
        },
        data: JSON.stringify({
            comentario: nuevoComentario,
            username: username
        }),
        success: function(response) {
            console.log('Comentario editado:', response);
            loadComments(videoId); // Volver a cargar los comentarios después de editar
        },
        error: function(error) {
            console.error('Error al editar el comentario:', error);
        }
    });
}


function abrirModalEdicion(comentarioId, textoComentario, videoId) {
    // Aquí deberías crear el HTML para el modal
    const modalHTML = $(`
        <div id="edit-modal" class="modal">
            <div class="modal-content">
                <span class="close-modal">&times;</span>
                <h2>Editar Comentario</h2>
                <textarea id="edit-comment-input">${textoComentario}</textarea>
                <button id="save-edit" data-id="${comentarioId}" data-video-id="${videoId}">Guardar Cambios</button>
            </div>
        </div>
    `);

    $('body').append(modalHTML);

    // Evento para cerrar el modal
    modalHTML.find('.close-modal').on('click', function() {
        modalHTML.remove();
    });

    // Evento para guardar el comentario editado
    modalHTML.find('#save-edit').on('click', function() {
        const nuevoComentario = $('#edit-comment-input').val();
        const comentarioId = $(this).data('id');
        const videoId = $(this).data('video-id');
        editarComentario(comentarioId, nuevoComentario, videoId);
        modalHTML.remove();
    });
}

function eliminarComentario(comentarioId, videoId) {
    $.ajax({
        url: `/comentarios/delete/${comentarioId}`,
        type: 'DELETE',
        headers: {
            'Authorization': 'Bearer ' + token,
            'Content-Type': 'application/json'
        },
        data: JSON.stringify({
            username: username
        }),
        success: function(response) {
            console.log(response);
            loadComments(videoId); // Recargar comentarios después de eliminar
        },
        error: function(error) {
            console.error('Error al eliminar el comentario:', error);
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
