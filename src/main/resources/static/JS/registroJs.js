// Función para alternar la visibilidad de la contraseña
function togglePasswordVisibility() {
    const passwordField = document.getElementById('usuariocontrasena');
    const eyeIcon = document.getElementById('eyeIcon');

    if (passwordField.type === 'password') {
        passwordField.type = 'text';
        eyeIcon.classList.remove('fa-eye');
        eyeIcon.classList.add('fa-eye-slash');
    } else {
        passwordField.type = 'password';
        eyeIcon.classList.remove('fa-eye-slash');
        eyeIcon.classList.add('fa-eye');
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
    password += lowercase.charAt(Math.floor(Math.random() * lowercase.length));
    password += uppercase.charAt(Math.floor(Math.random() * uppercase.length));
    password += numbers.charAt(Math.floor(Math.random() * numbers.length));
    password += specialChars.charAt(Math.floor(Math.random() * specialChars.length));

    // Rellenar el resto de la contraseña con caracteres aleatorios
    const allChars = lowercase + uppercase + numbers + specialChars;
    for (let i = 4; i < length; i++) {
        password += allChars.charAt(Math.floor(Math.random() * allChars.length));
    }

    // Mezclar los caracteres para que no siga siempre el mismo patrón
    password = password.split('').sort(() => 0.5 - Math.random()).join('');

    document.getElementById("usuariocontrasena").value = password;
}
$(document).ready(function() {
    $('#togglePassword').click(togglePasswordVisibility);
    $('#generatePasswordBtn').click(generatePassword);
});
// Función para guardar el usuario
function saveUsuario() {
    let name = $("#usuarioname").val();
    let mail = $("#usuariocorreo").val();
    let alias = $("#usuarioalias").val();
    let cel = $("#usuariocel").val();
    let contrasena = $("#usuariocontrasena").val();

    if (name === '' || mail === '' || alias === '' || cel === '' || contrasena === '') {
        alert('Por favor, complete todos los campos.');
        return; // Detener la ejecución si algún campo está vacío
    }
    // Obtener la imagen de perfil seleccionada
    const selectedImage = document.querySelector('input[name="profilePicture"]:checked');
    let profileImage = "";
    if (selectedImage) {
        profileImage = selectedImage.value;
    }

    // Verificar que se haya seleccionado una imagen
    if (profileImage === "") {
        alert("Por favor selecciona una imagen de perfil.");
        return;
    }

    // Crear un objeto de usuario para enviar al servidor
    let data = {
        nombre: name,
        username: alias,
        correo: mail,
        celular: cel,
        password: contrasena,
        perfil: profileImage
    };

    // Enviar los datos al servidor usando fetch o AJAX
    $.ajax({
        url: '/auth/register',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(data),
        success: function(response) {
            localStorage.setItem('token', response.token);
            alert('Usuario registrado correctamente.');
            window.location.href = "/Vistas/inicioVista.html";
        },
        error: function(xhr, status, error) {
            let errorMessage = 'Ocurrió un error inesperado. Inténtelo de nuevo.';
    
            // Verificar si el error es por alias ya en uso
            if (xhr.status === 400 && xhr.responseText === 'alias ya en uso') {
                errorMessage = 'El alias ya está en uso. Por favor, elija otro.';
            } 
            // Verificar si el error es por contraseñas no válidas
            else if (xhr.status === 400 && xhr.responseText === 'contraseña inválida') {
                errorMessage = 'La contraseña no cumple con los requisitos. Debe tener al menos 8 caracteres, incluir una letra mayúscula, un número y un carácter especial.';
            }
    
            alert(errorMessage);
            window.location.href = "/Vistas/registroVista.html";
        }
    });
}