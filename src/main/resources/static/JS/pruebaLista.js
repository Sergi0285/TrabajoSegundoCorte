let token = localStorage.getItem('token');

window.onload = function() {
    verificarTokenYRedireccionarALogin();
    
    const videosContainer = $('#videos-container');
    const videoTitle = $('#video-title');
    const videoDescription = $('#video-description');
    const videoSource = $('#video-source');
    const videoPlayer = $('#video-player');

    // Función para cargar los videos desde el servidor
    function loadVideos() {
        $.ajax({
            url: '/videos/Lista', // Ruta de la API para obtener la lista de videos
            type: 'GET',
            headers: {
                'Authorization': 'Bearer ' + token // Asegúrate de que 'token' esté definido
            },
            success: function (videos) {
                videos.forEach(video => {
                    // Crear un contenedor para cada video
                    const videoElement = $(`
                        <div class="video-item">
                            <h3>${video.titulo}</h3>
                            <p>${video.descripcion}</p>
                            <button class="play-button" data-id="${video.idVideo}">Reproducir</button>
                        </div>
                    `);

                    // Añadir el video a la lista
                    videosContainer.append(videoElement);
                });

                // Asignar evento click a cada botón de reproducción
                $('.play-button').on('click', function () {
                    const videoId = $(this).data('id');
                    console.log(videoId);
                    playVideo(videoId);
                });
            },
            error: function (error) {
                console.error('Error al cargar los videos:', error);
            }
        });
    }

    // Función para reproducir el video seleccionado
    function playVideo(identificador) {
        $.ajax({
            url: '/videos/ver', // Ruta de la API para obtener los detalles del video
            type: 'GET',
            headers: {
                'Authorization': 'Bearer ' + token // Asegúrate de que 'token' esté definido
            },
            data: {
                id: identificador
            },
            xhr: function() {
            // Crear un objeto XMLHttpRequest para poder manejar la respuesta como un Blob
                const xhr = new window.XMLHttpRequest();
                xhr.responseType = 'blob'; // Indicar que la respuesta será de tipo Blob
                return xhr;
            },
            success: function(videoBlob) {
                // Crear una URL para el Blob
                const videoObjectURL = URL.createObjectURL(videoBlob);
                
                // Establecer la fuente del reproductor de video
                videoSource.attr('src', videoObjectURL);

                // Cargar y reproducir el video
                videoPlayer[0].load(); // Cargar el nuevo video
                videoPlayer[0].play(); // Comenzar a reproducir el video
            },
            error: function(error) {
                console.error('Error al cargar el video:', error);
            }
        });
    }

    // Cargar los videos cuando se cargue la página
    loadVideos();
}

function verificarTokenYRedireccionarALogin() {
    // Verificar si el token está presente
    if (token === null) {
        window.location.href = '/Vistas/inicioVista.html';
    }
}