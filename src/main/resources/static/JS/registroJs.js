// Variable global para guardar la imagen de perfil
let profileImage = "";

// Función para alternar la visibilidad de la contraseña
function togglePasswordVisibility() {
    const passwordField = document.getElementById('usuariocontrasena');
    const eyeIcon = document.getElementById('eyeIcon');

    // Alterna el tipo de input y el icono del ojo
    if (passwordField.type === 'password') {
        passwordField.type = 'text';
        eyeIcon.classList.replace('fa-eye', 'fa-eye-slash');
    } else {
        passwordField.type = 'password';
        eyeIcon.classList.replace('fa-eye-slash', 'fa-eye');
    }
}

// Función para generar una contraseña segura automáticamente
function generatePassword() {
    const length = 8; // Longitud mínima de la contraseña
    const lowercase = "abcdefghijklmnopqrstuvwxyz";
    const uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    const numbers = "0123456789";
    const specialChars = "!@#$%^&*()_+~*'¿¡|}{[]:;?><,./-=";

    let password = "";

    // Garantizar al menos un carácter de cada tipo
    password += getRandomChar(lowercase);
    password += getRandomChar(uppercase);
    password += getRandomChar(numbers);
    password += getRandomChar(specialChars);

    // Rellenar el resto de la contraseña con caracteres aleatorios
    const allChars = lowercase + uppercase + numbers + specialChars;
    for (let i = 4; i < length; i++) {
        password += getRandomChar(allChars);
    }

    // Mezclar los caracteres para que no siga siempre el mismo patrón
    password = password.split('').sort(() => 0.5 - Math.random()).join('');

    document.getElementById("usuariocontrasena").value = password;
}

// Función auxiliar para obtener un carácter aleatorio
function getRandomChar(chars) {
    return chars.charAt(Math.floor(Math.random() * chars.length));
}

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

// Función para guardar el usuario
function saveUsuario() {
    console.log("Función saveUsuario llamada.");
    const name = $('#usuarioname').val();
    const mail = $('#usuariocorreo').val();
    const alias = $('#usuarioalias').val();
    const cel = $('#usuariocel').val();
    const contrasena = $('#usuariocontrasena').val();
    const imageInput = document.getElementById('profileImageInput'); // Input de imagen
    const defaultImageSelect = $('#defaultImageSelect').val(); // Obtener la opción de imagen por defecto

    // Verificar que todos los campos estén completos
    if (!name || !mail || !alias || !cel || !contrasena) {
        alert('Por favor, completa todos los campos.');
        return;
    }

    const formData = new FormData();
    formData.append('nombre', name);
    formData.append('username', alias);
    formData.append('correo', mail);
    formData.append('celular', cel);
    formData.append('password', contrasena);

    // Función para manejar el envío del formulario
    function submitFormData(file) {
        formData.append('perfil', file);
        $.ajax({
            url: '/auth/register',
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: function(response) {
                localStorage.setItem('token', response.token);
                alert('Usuario registrado correctamente.');
                window.location.href = "/Vistas/inicioVista.html";
            },
            error: function(xhr) {
                let errorMessage = 'Ocurrió un error inesperado. Inténtelo de nuevo.';
                if (xhr.status === 400) {
                    switch (xhr.responseText) {
                        case 'alias ya en uso':
                            errorMessage = 'El alias ya está en uso. Por favor, elija otro.';
                            break;
                        case 'contraseña inválida':
                            errorMessage = 'La contraseña no cumple con los requisitos. Debe tener al menos 8 caracteres, incluir una letra mayúscula, un número y un carácter especial.';
                            break;
                    }
                }
                alert(errorMessage);
            }
        });
    }

    // Manejo de imagen subida o por defecto
    if (imageInput.files.length > 0) {
        const file = imageInput.files[0];
        console.log("Imagen subida por el usuario:", file);
        submitFormData(file);
    } else if (defaultImageSelect) {
        loadDefaultImage(defaultImageSelect, submitFormData);
    } else {
        alert('Por favor, selecciona una imagen de perfil.');
    }
}

// Función para previsualizar la imagen cargada
function previewImage(event) {
    const previewContainer = document.getElementById('customImagePreview');
    previewContainer.src = ""; // Limpiar cualquier imagen previa
    previewContainer.style.display = 'none'; // Ocultar la vista previa de la imagen

    const file = event.target.files[0];

    if (file) {
        const reader = new FileReader();

        reader.onload = function() {
            previewContainer.src = reader.result;
            previewContainer.style.display = 'block'; // Mostrar la vista previa de la imagen
        };

        reader.readAsDataURL(file);
    }
}

// Inicialización de eventos con jQuery
$(document).ready(function() {
    $('#togglePassword').click(togglePasswordVisibility);
    $('#generatePasswordBtn').click(generatePassword);
    $('#profileImageInput').change(previewImage); // Evento para previsualizar la imagen

    // Manejar la selección de imágenes predeterminadas
    $('#defaultImageSelect').change(function() {
        const selectedImage = $(this).val();
        const previewContainer = document.getElementById('customImagePreview');

        if (selectedImage) {
            previewContainer.src = selectedImage; // Cambiar la fuente de la imagen de vista previa
            previewContainer.style.display = 'block'; // Mostrar la imagen de vista previa
        } else {
            previewContainer.style.display = 'none'; // Ocultar la imagen de vista previa si no hay selección
        }
    });

    $('#saveButton').click(function() {
        console.log("Botón Guardar presionado.");
        saveUsuario(); // Llama a la función saveUsuario
    });
});