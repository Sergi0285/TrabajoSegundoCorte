let token = localStorage.getItem('token');
let identificador = localStorage.getItem('id_video');

var tokenParts = token.split('.');
var tokenPayload = JSON.parse(atob(tokenParts[1]));
var username = tokenPayload.sub;

console.log(username);
console.log(identificador);

let currentEditingCommentId = null;

$(document).ready(function() {
    verificarTokenYRedireccionarALogin();
    playVideo(identificador); 
    
    $('#submit-comment').on('click', function() {
        const videoId = $(this).data('video-id');
        const comentario = $('#comment-input').val();

        if (comentario) {
            agregarComentario(videoId, comentario);
            $('#comment-input').val('');
        }
    });

    $('#save-edit-comment').on('click', function() {
        const newComment = $('#edit-comment-input').val();
        if (currentEditingCommentId && newComment) {
            editarComentario(currentEditingCommentId, newComment);
            $('#editCommentModal').hide();
            $('#edit-comment-input').val('');
        }
    });

    $('#close-modal').on('click', function() {
        $('#editCommentModal').hide();
    });

    // Evento para el botón de suscripción
    $('#subscribe-button').on('click', function() {
        const canalId = identificador;
        const estaSuscrito = $(this).hasClass('btn-danger');
        
        if (estaSuscrito) {
            cancelarSuscripcion(canalId);
        } else {
            suscribirse(canalId);
        }
    });
});

function verificarTokenYRedireccionarALogin() {
    if (token === null) {
        window.location.href = '/Vistas/inicioVista.html';
    }
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
            $('#submit-comment').data('video-id', identificador);
            loadComments(identificador);
            verificarSuscripcion(identificador); // Verificar estado de suscripción
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

                if (isOwner) {
                    comentarioElement.find('.options-menu').on('click', function() {
                        $(this).find('.options-menu-content').toggle();
                    });

                    comentarioElement.find('.edit-comment').on('click', function(e) {
                        e.preventDefault();
                        currentEditingCommentId = $(this).data('id');
                        const currentCommentText = $(this).data('comment');
                        $('#edit-comment-input').val(currentCommentText);
                        $('#editCommentModal').show();
                    });

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
        url: `/comentarios/editar/${comentarioId}`,
        type: 'PUT',
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
            loadComments(identificador);
        },
        error: function(error) {
            console.error('Error al editar el comentario:', error);
        }
    });
}

function abrirModalEdicion(comentarioId, textoComentario, videoId) {
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

    modalHTML.find('.close-modal').on('click', function() {
        modalHTML.remove();
    });

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
            loadComments(videoId);
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
            loadComments(videoId);
        },
        error: function(error) {
            console.error('Error al agregar el comentario:', error);
        }
    });
}

// Nuevas funciones para el manejo de suscripciones
function verificarSuscripcion(canalId) {
    $.ajax({
        url: `/suscripciones/verificar?username=${username}&canalId=${canalId}`,
        type: 'GET',
        headers: {
            'Authorization': 'Bearer ' + token
        },
        success: function(estaSuscrito) {
            actualizarBotonSuscripcion(estaSuscrito);
        },
        error: function(error) {
            console.error('Error al verificar suscripción:', error);
        }
    });
}

function actualizarBotonSuscripcion(estaSuscrito) {
    const boton = $('#subscribe-button');
    if (estaSuscrito) {
        boton.text('Cancelar Suscripción');
        boton.removeClass('btn-primary').addClass('btn-danger');
    } else {
        boton.text('Suscribirse');
        boton.removeClass('btn-danger').addClass('btn-primary');
    }
}

function suscribirse(canalId) {
    $.ajax({
        url: '/suscripciones/suscribirse',
        type: 'POST',
        headers: {
            'Authorization': 'Bearer ' + token,
            'Content-Type': 'application/json'
        },
        data: JSON.stringify({
            username: username,
            canalId: canalId
        }),
        success: function(response) {
            actualizarBotonSuscripcion(true);
            console.log('Suscripción exitosa:', response);
        },
        error: function(error) {
            console.error('Error al suscribirse:', error);
        }
    });
}

function cancelarSuscripcion(canalId) {
    $.ajax({
        url: '/suscripciones/cancelar',
        type: 'DELETE',
        headers: {
            'Authorization': 'Bearer ' + token,
            'Content-Type': 'application/json'
        },
        data: JSON.stringify({
            username: username,
            canalId: canalId
        }),
        success: function(response) {
            actualizarBotonSuscripcion(false);
            console.log('Suscripción cancelada:', response);
        },
        error: function(error) {
            console.error('Error al cancelar suscripción:', error);
        }
    });
}