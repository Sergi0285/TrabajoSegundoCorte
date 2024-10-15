package Trabajo.Ingenieria.Servicios;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import Trabajo.Ingenieria.Controladores.authRespuesta;
import Trabajo.Ingenieria.Controladores.inicioPeticion;
import Trabajo.Ingenieria.Controladores.registroPeticion;
import Trabajo.Ingenieria.Entidades.role;
import Trabajo.Ingenieria.Entidades.usuario;
import Trabajo.Ingenieria.Repositorios.usuarioRepositorio;
import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class authServicio {
    private final usuarioRepositorio userRepository;
    private final jwtServicio jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    // Método para validar la seguridad de la contraseña
    private boolean isPasswordSecure(String password) {
        int minLength = 8;
        boolean hasUpperCase = password.chars().anyMatch(Character::isUpperCase);
        boolean hasNumber = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecialChar = password.chars().anyMatch(ch -> "!@#$%^&*(),.?\":{}|<>".indexOf(ch) >= 0);
        return password.length() >= minLength && hasUpperCase && hasNumber && hasSpecialChar;
    }

    // Método para el inicio de sesión
    public authRespuesta login(inicioPeticion request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        UserDetails usuario = userRepository.findByUsername(request.getUsername()).orElseThrow();
        String token = jwtService.getToken(usuario);
        return authRespuesta.builder()
            .token(token)
            .build();
    }

     // Método para registrar un nuevo usuario con imagen de perfil
    @SuppressWarnings("null")
    public authRespuesta register(registroPeticion request, MultipartFile profileImage) {
        // Validar la seguridad de la contraseña
        if (!isPasswordSecure(request.getPassword())) {
            throw new IllegalArgumentException("La contraseña no cumple con los criterios de seguridad: " +
                "Debe tener al menos 8 caracteres, incluir una letra mayúscula, un número y un carácter especial.");
        }
    
        // Validar que el alias no esté en uso
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("El alias ya está en uso. Por favor, elija otro.");
        }
    
        // Validar que la imagen de perfil no esté vacía
        if (profileImage.isEmpty()) {
            throw new IllegalArgumentException("La imagen de perfil no puede estar vacía.");
        }
    
        // Validar que el tipo de contenido de la imagen sea adecuado (opcional)
        String contentType = profileImage.getContentType();
        if (!contentType.startsWith("image/")) {
            throw new IllegalArgumentException("El archivo de imagen debe ser una imagen válida.");
        }
    
        // Renombrar la imagen de perfil usando el alias del usuario
        String fileName = request.getUsername() + "_.jpg";
        String uploadDir = "recursos/videos/imagenes";
        Path uploadPath = Paths.get(uploadDir);
    
        // Crear el directorio si no existe y guardar la imagen
        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
    
            // Guardar la imagen en el directorio especificado
            try (InputStream inputStream = profileImage.getInputStream()) {
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Error al guardar la imagen de perfil: " + e.getMessage());
        }
    
        // Crear el usuario y guardar la ruta de la imagen en la base de datos
        usuario user = usuario.builder()
            .nombre(request.getNombre())
            .username(request.getUsername())
            .correo(request.getCorreo())
            .celular(request.getCelular())
            .perfil("recursos/videos/imagenes/" + fileName)  // Guardar la ruta de la imagen
            .password(passwordEncoder.encode(request.getPassword()))
            .rol(role.USER)
            .build();
    
        // Guardar el usuario en la base de datos
        userRepository.save(user);
    
        // Retornar el token de autenticación
        return authRespuesta.builder()
            .token(jwtService.getToken(user))
            .build();
    }
}
