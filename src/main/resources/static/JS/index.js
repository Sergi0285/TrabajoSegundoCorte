let token = localStorage.getItem('token');

var tokenParts = token.split('.');
var tokenPayload = JSON.parse(atob(tokenParts[1]));
var username = tokenPayload.sub;

console.log(username);

$(document).ready(function() {
    loadRandomVideos();
});

function loadRandomVideos() {
    $.ajax({
        url: '/videos/randomVideos',
        type: 'GET',
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
}

function loadImage(videoId) {
    $.ajax({
        url: '/videos/miniatura', // Asegúrate de que esta sea la ruta correcta
        type: 'GET',
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
