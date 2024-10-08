package Trabajo.Ingenieria.Servicios;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
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

    // Método para registrar un nuevo usuario
    public authRespuesta register(registroPeticion request) {
        // Validar la seguridad de la contraseña
        if (!isPasswordSecure(request.getPassword())) {
            throw new IllegalArgumentException("La contraseña no cumple con los criterios de seguridad: " +
                "Debe tener al menos 8 caracteres, incluir una letra mayúscula, un número y un carácter especial.");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("El alias ya está en uso. Por favor, elija otro.");
        }
        // Crear y guardar el usuario
        usuario user = usuario.builder()
            .nombre(request.getNombre())
            .username(request.getUsername())
            .correo(request.getCorreo())
            .celular(request.getCelular())
            .perfil(request.getPerfil())
            .password(passwordEncoder.encode(request.getPassword()))
            .rol(role.USER)
            .build();

        userRepository.save(user);

        // Retornar el token de autenticación
        return authRespuesta.builder()
            .token(jwtService.getToken(user))
            .build();
    }
}
