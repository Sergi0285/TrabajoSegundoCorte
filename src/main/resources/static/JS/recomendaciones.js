let token = localStorage.getItem('token');

var tokenParts = token.split('.');
var tokenPayload = JSON.parse(atob(tokenParts[1]));
var username = tokenPayload.sub;

console.log(token);
console.log(username);

$(document).ready(function() {
    verificarTokenYRedireccionarALogin();
    cargarRecomendacionesVideos(username);
});

function verificarTokenYRedireccionarALogin() {
    // Verificar si el token está presente
    if (token === null) {
        window.location.href = '/Vistas/inicioVista.html';
    }
    checkToken();
}

function isTokenExpired(token) {
    if (!token) return true; // Si no hay token, considera que ha expirado
    const payload = JSON.parse(atob(token.split('.')[1])); // Decodifica el payload del JWT
    const expiration = payload.exp * 1000; // Convierte a milisegundos
    return Date.now() > expiration; // Compara la fecha de expiración con la fecha actual
}

// Función para cerrar sesión
function logout() {
    // Eliminar el token de localStorage
    localStorage.removeItem('token');

    // Redirigir al usuario a la página de inicio de sesión
    window.location.href = "/Vistas/inicioVista.html";
}

// Función para comprobar el estado del token al cargar la página
function checkToken() {
    const token = localStorage.getItem('token');
    if (isTokenExpired(token)) {
        alert('Tu sesión ha expirado. Por favor, inicia sesión nuevamente.');
        logout();
    }
}

function cargarRecomendacionesVideos(username) {
    $.ajax({
        url: `/videos/recomendaciones?username=${username}`, // Incluye el nombre de usuario en la URL
        type: 'GET',
        headers: {
            'Authorization': 'Bearer ' + token
        },
        success: function(videos) {
            const videosContainer = $('#recomendacionesContainer');
            videosContainer.empty();

            let rowDiv = $('<div class="row"></div>');

            videos.forEach((video, index) => {
                const videoElement = $(`
                    <div class="col-lg-4 col-md-6 col-sm-6">
                        <div class="product__item">
                            <div class="product__item__pic set-bg" id="miniatura-${video.idVideo}" data-setbg=""></div>
                            <div class="product__item__text">
                                <ul id="categorias-${video.idVideo}"></ul>
                                <h5><a href="#" class="video-title" data-id="${video.idVideo}">${video.titulo}</a></h5>
                            </div>
                            <div class="ep">${video.descripcion}</div>
                            <div class="view"><i class="fa fa-date"></i> ${video.fechaSubida}</div>
                        </div>
                    </div>
                `);

                rowDiv.append(videoElement);

                // Cargar las categorías del video
                loadCategorias(video.idVideo);

                // Asignar evento click para guardar el ID del video
                videoElement.find('.video-title').on('click', function(e) {
                    e.preventDefault();
                    const videoId = $(this).data('id');
                    guardarIdVideoVisualizacion(videoId, username);  // Guardar ID del video
                    guardarIdVideo(videoId); // Guardar el ID del video
                });

                // Cargar la miniatura del video como fondo
                loadImage(video.idVideo);
                console.log("OUHFAUOUDAUOWD");
                console.log("HP DANIEL LO DETESTO ", username);
                console.log("Videos cargados:", $('#recomendacionesContainer').html());
                

                // Añadir nueva fila cada 5 videos
                if ((index + 1) % 5 === 0) {
                    videosContainer.append(rowDiv);  // Añadir la fila después de 5 videos
                    rowDiv = $('<div class="row"></div>');  // Crear una nueva fila
                }
            });

            // Añadir la última fila si tiene videos
            if (rowDiv.children().length > 0) {
                videosContainer.append(rowDiv);  // Añadir cualquier fila restante
            }
        },
        error: function(error) {
            console.error('Error al cargar los videos:', error);
            const videosContainer = $('#recomendacionesContainer');
            videosContainer.empty().append('<p>Error al cargar las recomendaciones.</p>');
        }
    });
}

function loadCategorias(videoId) {
    $.ajax({
        url: `/categoria/categorias/${videoId}`, // Ajuste en la URL para incluir el id del video
        type: 'GET',
        headers: {
            'Authorization': 'Bearer ' + token
        },
        success: function(categorias) {
            const ulElement = $(`#categorias-${videoId}`);
            ulElement.empty();

            // Añadir las categorías al <ul> correspondiente
            categorias.forEach(videoCategoria => {
                ulElement.append(`<li>${videoCategoria.categoria}</li>`);
            });
        },
        error: function(error) {
            console.error('Error al cargar las categorías:', error);
        }
    });
}

function loadImage(videoId) {
    $.ajax({
        url: '/videos/miniatura', // Ruta correcta
        type: 'GET',
        headers: {
            'Authorization': 'Bearer ' + token
        },
        data: { id: videoId },
        xhr: function() {
            const xhr = new window.XMLHttpRequest();
            xhr.responseType = 'blob'; // Indicar que la respuesta es un blob
            return xhr;
        },
        success: function(imageBlob) {
            const imageObjectURL = URL.createObjectURL(imageBlob);
            const videoElement = $(`#miniatura-${videoId}`);
            videoElement.css('background-image', `url(${imageObjectURL})`);
        },
        error: function(error) {
            console.error('Error al cargar la miniatura:', error);
        }
    });
}

function guardarIdVideoVisualizacion(videoId, username) {
    $.ajax({
        url: '/visualizaciones/guardar',
        type: 'POST',
        headers: {
            'Authorization': 'Bearer ' + token  // Asegúrate de que el token es correcto
        },
        contentType: 'application/json',  // Enviar como JSON
        data: JSON.stringify({
            usuarioid: username,  // Asegúrate de que sea "usuarioid", no "username"
            videoid: videoId
        }),
        success: function(response) {
            console.log("Visualización guardada correctamente:", response);
        },
        error: function(error) {
            console.error("Error al guardar la visualización:", error);
        }
    });
}


function guardarIdVideo(id) {
    localStorage.setItem('id_video', id);
    window.location.href = '/Vistas/reproductorVideo.html';
}
