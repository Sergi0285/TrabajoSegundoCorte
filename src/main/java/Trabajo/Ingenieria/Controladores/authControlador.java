package Trabajo.Ingenieria.Controladores;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import Trabajo.Ingenieria.Servicios.authServicio;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class authControlador {
    private final authServicio authService;
    
    @PostMapping(value = "login")
    public ResponseEntity<authRespuesta> login(@RequestBody inicioPeticion request)
    {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping(value = "register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<authRespuesta> register(
        @RequestPart("nombre") String nombre,
        @RequestPart("username") String username,
        @RequestPart("correo") String correo,
        @RequestPart("celular") String celular,
        @RequestPart("password") String password,
        @RequestPart("perfil") MultipartFile profileImage) {
        
        // Crear la instancia de registroPeticion si es necesario
        registroPeticion request = new registroPeticion(nombre, username, correo, celular, password);
        
        authRespuesta response = authService.register(request, profileImage);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleInvalidRequestException(IllegalArgumentException ex) {
        String message = ex.getMessage();
        if (message.contains("alias ya está en uso")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Alias ya en uso. Por favor, elija otro.");
        } else if (message.contains("contraseña")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Contraseña inválida. Debe cumplir con los requisitos de seguridad.");
        } else if (message.contains("imagen de perfil")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La imagen de perfil no puede estar vacía o no es válida.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ocurrió un error inesperado: " + message);
        }
    }
}
