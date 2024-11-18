let token = localStorage.getItem('token');

var tokenParts = token.split('.');
var tokenPayload = JSON.parse(atob(tokenParts[1]));
var username = tokenPayload.sub;

console.log(username);

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

$(document).ready(function() {
    verificarTokenYRedireccionarALogin()
    loadUserProfile(username);
});

// Función para cargar la información del perfil del usuario
function loadUserProfile(username) {
    $.ajax({
        url: `/controladorCliente/findbyalias?username=${username}`,
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + token
        },
        success: function(data) {
            // Muestra los datos del usuario en los campos
            $('#usuarioname').val(data.nombre);
            $('#usuariocorreo').val(data.correo);
            $('#usuariocel').val(data.celular);
            
            // Cargar la imagen de perfil
            loadProfileImage(username);
        },
        error: function(xhr, status, error) {
            console.error('Error al cargar el perfil del usuario:', error);
        }
    });
}

// Función para cargar la imagen de perfil
function loadProfileImage(username) {
    $.ajax({
        url: `/controladorCliente/perfil/${username}/imagen`,
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + token
        },
        xhr: function() {
            const xhr = new window.XMLHttpRequest();
            xhr.responseType = 'blob'; // Indicar que la respuesta es un blob
            return xhr;
        },
        success: function(imageBlob) {
            const imageUrl = URL.createObjectURL(imageBlob); // Crear la URL del blob
            $('#currentProfileImage').attr('src', imageUrl).show(); // Establecer la fuente de la imagen
        },
        error: function(xhr, status, error) {
            console.error('Error al cargar la imagen de perfil:', error);
        }
    });
}

function updateUser() {
    let token = localStorage.getItem('token');

    var tokenParts = token.split('.');
    var tokenPayload = JSON.parse(atob(tokenParts[1]));
    var username = tokenPayload.sub;

    const nombre = $('#usuarioname').val();
    const correo = $('#usuariocorreo').val();
    const celular = $('#usuariocel').val();

    const perfilImageInput = $('#profileImageInput')[0]; // Input de archivo
    const defaultImageSelect = $('#defaultImageSelect').val(); // Selección de imagen predeterminada

    let perfilImage = perfilImageInput && perfilImageInput.files.length > 0 ? perfilImageInput.files[0] : null;

    const formData = new FormData();
    formData.append('username', username);
    formData.append('nombre', nombre);
    formData.append('correo', correo);
    formData.append('celular', celular);

    // Función para enviar el formulario después de cargar la imagen (personalizada o por defecto)
    function sendForm(perfilImage) {
        if (perfilImage) {
            formData.append('perfilImage', perfilImage);
        }

        $.ajax({
            url: '/controladorCliente/actualizarUs',
            method: 'PUT',
            headers: {
                'Authorization': 'Bearer ' + token
            },
            data: formData,
            processData: false,
            contentType: false,
            success: function(response) {
                console.log('Usuario actualizado:', response);
                alert("¡Usuario actualizado correctamente!");
            },
            error: function(xhr, status, error) {
                console.error('Error al actualizar el usuario:', error);
            }
        });
    }

    // Si el usuario selecciona una imagen personalizada, la usamos
    if (perfilImage) {
        sendForm(perfilImage);
    } else if (defaultImageSelect) {
        // Si no hay imagen personalizada, usamos la imagen por defecto
        loadDefaultImage(defaultImageSelect, function(imageFile) {
            sendForm(imageFile);
        });
    } else {
        // Si no se selecciona imagen personalizada ni predeterminada, solo actualiza el usuario
        sendForm(null);
    }
}

// Llama a la función updateUser() cuando se envíe el formulario o se haga clic en un botón
$('#updateButton').on('click', function() {
    updateUser();
});


// Función para cargar la imagen por defecto y convertirla a un archivo
function loadDefaultImage(imageUrl, callback) {
    const img = new Image();
    img.crossOrigin = "Anonymous"; // Necesario para evitar problemas de CORS
    img.onload = function() {
        const canvas = document.createElement('canvas');
        canvas.width = img.width;
        canvas.height = img.height;
        const ctx = canvas.getContext('2d');
        ctx.drawImage(img, 0, 0);
        canvas.toBlob(function(blob) {
            callback(new File([blob], 'defaultProfileImage.png', { type: 'image/png' }));
        }, 'image/png');
    };
    img.src = imageUrl;
}


// Función para previsualizar la imagen del archivo seleccionado
function previewImage(event) {
    const previewContainer = document.getElementById('customImagePreview');
    previewContainer.src = ""; // Limpiar cualquier imagen previa
    previewContainer.style.display = 'none'; // Ocultar la vista previa de la imagen

    const file = event.target.files[0]; // Obtener el archivo seleccionado

    if (file) {
        const reader = new FileReader();

        reader.onload = function() {
            previewContainer.src = reader.result; // Establecer la imagen de vista previa
            previewContainer.style.display = 'block'; // Mostrar la vista previa de la imagen
        };

        reader.readAsDataURL(file); // Leer el archivo como URL de datos
    }
}

// Función para manejar la selección de imagen predeterminada
function handleDefaultImageSelect(event) {
    const selectedImageUrl = event.target.value;
    const previewContainer = document.getElementById('customImagePreview');

    if (selectedImageUrl) {
        loadDefaultImage(selectedImageUrl, function(file) {
            const reader = new FileReader();
            reader.onload = function() {
                previewContainer.src = reader.result; // Establecer la imagen de vista previa
                previewContainer.style.display = 'block'; // Mostrar la vista previa de la imagen
            };
            reader.readAsDataURL(file); // Leer el archivo como URL de datos
        });
    } else {
        previewContainer.src = ""; // Limpiar la vista previa si no hay selección
        previewContainer.style.display = 'none'; // Ocultar la vista previa
    }
}

// Agregar el listener al input de archivo para previsualizar la imagen
document.getElementById('profileImageInput').addEventListener('change', previewImage);

// Agregar el listener al select de imágenes predeterminadas
document.getElementById('defaultImageSelect').addEventListener('change', handleDefaultImageSelect);
