package Trabajo.Ingenieria.Controladores;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import Trabajo.Ingenieria.Entidades.usuario;
import Trabajo.Ingenieria.Servicios.clienteServicio;
import io.jsonwebtoken.io.IOException;

@RestController
@RequestMapping("/controladorCliente")
public class clienteControlador {
     @Autowired
private clienteServicio usuarioService;
    @GetMapping("/usuario")
    public List<usuario> getAllUs(){
        return usuarioService.getAllUsuarios();
    }

    @PostMapping("/guardarUs")
    public usuario guardarUsuario(@RequestBody usuario k){
        return usuarioService.save(k);
    }
    @GetMapping("/findbyalias")
    public usuario findByUsername(@RequestParam("username") String username){
        return usuarioService.findByUsername(username);
    }
    @GetMapping("/rol")
    public String obtenerRolPorUsuario() {
        // Obtener el nombre de usuario del objeto Authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        // Buscar el usuario en el servicio Usuario_modelos
        usuario user = usuarioService.findByUsername(username);

        // Verificar si el usuario existe y devolver su rol
        if (user != null) {
            return user.getRol().toString();
        } else {
            return "Usuario no encontrado";
        }
    }

    // Endpoint para obtener la imagen de perfil por el username
    @GetMapping("/perfil/{username}/imagen")
    public ResponseEntity<Resource> obtenerImagenPerfil(@PathVariable String username) throws IOException {
        // Llama al servicio para obtener el recurso de la imagen
        return usuarioService.streamProfileImage(username);
    }

    @PutMapping("/actualizarUs")
    public ResponseEntity<String> actualizarUsuario(@RequestParam("username") String username, 
                                                    @RequestParam("nombre") String nombre,
                                                    @RequestParam("correo") String correo,
                                                    @RequestParam("celular") String celular,
                                                    @RequestParam(value = "perfilImage", required = false) MultipartFile perfilImage) {
        // Manejo de excepciones
        try {
            // Llamar al servicio para obtener el usuario existente
            usuario usuarioExistente = usuarioService.findByUsername(username);
            
            if (usuarioExistente == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
            }
    
            // Actualizar los campos que han sido modificados
            usuarioExistente.setNombre(nombre);
            usuarioExistente.setCorreo(correo);
            usuarioExistente.setCelular(celular);
    
            // Si hay una nueva imagen de perfil, manejarla
            if (perfilImage != null && !perfilImage.isEmpty()) {
                // Renombrar la imagen de perfil usando el alias del usuario
                String fileName = username + "_.jpg";
                String uploadDir = "recursos/videos/imagenes";
                Path uploadPath = Paths.get(uploadDir);
    
                // Crear el directorio si no existe y guardar la imagen
                try {
                    // Manejo de IOException al crear directorios
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }
    
                    // Guardar la imagen en el directorio especificado
                    try (InputStream inputStream = perfilImage.getInputStream()) {
                        Path filePath = uploadPath.resolve(fileName);
                        Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                        // Actualizar la ruta de la imagen en el objeto usuario
                        usuarioExistente.setPerfil("recursos/videos/imagenes/" + fileName);
                    } catch (IOException e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                             .body("Error al guardar la imagen de perfil: " + e.getMessage());
                    }
                } catch (IOException e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                         .body("Error al crear el directorio: " + e.getMessage());
                }
            }
    
            // Guardar el usuario actualizado
            usuarioService.update(usuarioExistente);
            return ResponseEntity.ok("Usuario actualizado con éxito");
    
        } catch (Exception e) {  // Captura de cualquier otra excepción
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error en la actualización: " + e.getMessage());
        }
    }
    
}
