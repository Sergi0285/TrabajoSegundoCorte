package Trabajo.Ingenieria.Controladores;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import Trabajo.Ingenieria.Servicios.authServicio;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class authControlador {
    private final authServicio AuthService;
    
    @PostMapping(value = "login")
    public ResponseEntity<authRespuesta> login(@RequestBody inicioPeticion request)
    {
        return ResponseEntity.ok(AuthService.login(request));
    }

    @PostMapping(value = "register")
    public ResponseEntity<authRespuesta> register(@RequestBody registroPeticion request)
    {
        return ResponseEntity.ok(AuthService.register(request));
    }  
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleInvalidRequestException(IllegalArgumentException ex) {
        String message = ex.getMessage();
        if (message.contains("alias ya está en uso")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("alias ya en uso");
        } else if (message.contains("contraseña")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("contraseña inválida");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ocurrió un error inesperado");
        }
    }
}
