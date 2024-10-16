let token = localStorage.getItem('token');

var tokenParts = token.split('.');
var tokenPayload = JSON.parse(atob(tokenParts[1]));
var username = tokenPayload.sub;

console.log(token);
console.log(username);

$(document).ready(function() {
    verificarTokenYRedireccionarALogin()
    loadRandomVideos();
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

/*function loadRandomVideos() {
    $.ajax({
        url: '/videos/randomVideos',
        type: 'GET',
        headers: {
            'Authorization': 'Bearer ' + token
        },
        success: function(videos) {
            const videosContainer = $('#videos-container');
            videosContainer.empty();

            let rowDiv = $('<div class="row"></div>');

            videos.forEach((video, index) => {
                // Crea un elemento div para cada video
                console.log(video.videoCategorias);
                const videoElement = $(`
                    <div class="product__item">
                        <div class="product__item__pic" id="miniatura-${video.idVideo}"></div>
                            <div class="ep">${video.descripcion}</div>
                            <div class="comment"><i class="fa fa-comments"></i> 15</div>
                            <div class="view"><i class="fa fa-eye"></i> 9141</div>
                        <div class="product__item__text">
                            <ul>
                                <li>Active</li>
                                <li>Movie</li>
                            </ul>
                            <h5><a href="#" class="video-title" data-id="${video.idVideo}">${video.titulo}</a></h5>
                        </div>
                    </div>
                `);

                rowDiv.append(videoElement);

                // Asignar evento click para guardar el ID del video
                videoElement.find('.video-title').on('click', function(e) {
                    e.preventDefault();
                    const videoId = $(this).data('id');
                    guardarIdVideo(videoId); // Guardar el ID del video
                });

                // Cargar la miniatura
                loadImage(video.idVideo);

                if ((index + 1) % 3 === 0) {
                    videosContainer.append(rowDiv);
                    rowDiv = $('<div class="row"></div>');
                }
            });

            if (rowDiv.children().length > 0) {
                videosContainer.append(rowDiv);
            }
        },
        error: function(error) {
            console.error('Error al cargar los videos:', error);
        }
    });
}*/

function loadRandomVideos() {
    $.ajax({
        url: '/videos/randomVideos',
        type: 'GET',
        headers: {
            'Authorization': 'Bearer ' + token
        },
        success: function(videos) {
            const videosContainer = $('#videos-container');
            videosContainer.empty();

            let rowDiv = $('<div class="row"></div>');

            videos.forEach((video, index) => {
                // Crea un elemento div para cada video
                const videoElement = $(`
                    <div class="product__item">
                        <div class="product__item__pic" id="miniatura-${video.idVideo}"></div>
                        <div class="product__item__text">
                            <ul id="categorias-${video.idVideo}"></ul> <!-- Aquí irán las categorías -->
                            <h5><a href="#" class="video-title" data-id="${video.idVideo}">${video.titulo}</a></h5>
                        </div>
                            <div class="ep">${video.descripcion}</div>
                            <div class="view"><i class="fa fa-date"></i> ${video.fechaSubida}</div>
                        
                    </div>
                `);

                rowDiv.append(videoElement);

                // Llamada AJAX para cargar las categorías del video
                loadCategorias(video.idVideo);

                // Asignar evento click para guardar el ID del video
                videoElement.find('.video-title').on('click', function(e) {
                    e.preventDefault();
                    const videoId = $(this).data('id');
                    guardarIdVideo(videoId); // Guardar el ID del video
                });

                // Cargar la miniatura
                loadImage(video.idVideo);

                if ((index + 1) % 3 === 0) {
                    videosContainer.append(rowDiv);
                    rowDiv = $('<div class="row"></div>');
                }
            });

            if (rowDiv.children().length > 0) {
                videosContainer.append(rowDiv);
            }
        },
        error: function(error) {
            console.error('Error al cargar los videos:', error);
        }
    });
}

function loadCategorias(videoId) {
    // Hacer la llamada AJAX para obtener las categorías de un video específico
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
                ulElement.append(`<li>${videoCategoria.categoria}</li>`); // Accediendo a la propiedad 'categoria'
            });
        },
        error: function(error) {
            console.error('Error al cargar las categorías:', error);
        }
    });
}


function loadImage(videoId) {
    $.ajax({
        url: '/videos/miniatura', // Asegúrate de que esta sea la ruta correcta
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
            // Crear un elemento <img> y añadirlo al contenedor
            const imgElement = $(`<img src="${imageObjectURL}" alt="Miniatura de video ${videoId}" style="width: 100%; height: auto;">`);
            $(`#miniatura-${videoId}`).append(imgElement); // Añadir la imagen al div
        },
        error: function(error) {
            console.error('Error al cargar la miniatura:', error);
        }
    });
}



function guardarIdVideo(id) {
    localStorage.setItem('id_video', id);
    window.location.href = '/Vistas/reproductorVideo.html';
}
