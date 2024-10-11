package Trabajo.Ingenieria.Controladores;

import java.util.Map;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import Trabajo.Ingenieria.Entidades.comentarios;
import Trabajo.Ingenieria.Entidades.usuario;
import Trabajo.Ingenieria.Entidades.videos;
import Trabajo.Ingenieria.Servicios.clienteServicio;
import Trabajo.Ingenieria.Servicios.comentarioServicio;
import Trabajo.Ingenieria.Servicios.videosServicio;

@RestController
@RequestMapping("/comentarios")
public class comentarioControlador {
    @Autowired
    private clienteServicio clienteServicio;

    @Autowired
    private comentarioServicio comentarioServicio;

    @Autowired
    private videosServicio videoService;

    @PostMapping("/add")
    public ResponseEntity<?> addComentario(@RequestBody Map<String, String> request) {
        String comentarioText = request.get("comentario");
        String username = request.get("usuario");
        Long videoId = Long.valueOf(request.get("video"));

        // Obtener el video
        videos video = videoService.findById(videoId);
        if (video == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Video no encontrado");
        }

        // Obtener el usuario por nombre de usuario
        usuario user = clienteServicio.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        // Crear el comentario
        comentarios comentario = new comentarios();
        comentario.setComentario(comentarioText);
        comentario.setFechaComentario(new java.sql.Date(System.currentTimeMillis()));
        comentario.setVideo(video);
        comentario.setUsuario(user); // Asignar el usuario encontrado

        // Guardar el comentario
        comentarioServicio.addComentario(comentario);

        return ResponseEntity.ok("Comentario guardado exitosamente");
    }


    // Método para eliminar un comentario
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> eliminarComentario(@PathVariable Long id) {
        boolean eliminado = comentarioServicio.deleteComentario(id);
        if (eliminado) {
            return ResponseEntity.ok("Comentario eliminado exitosamente");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Método para obtener todos los comentarios de un video
    @GetMapping("/video/{videoId}")
    public ResponseEntity<List<comentarios>> obtenerComentariosPorVideo(@PathVariable Long videoId) {
        List<comentarios> comentariosList = comentarioServicio.getComentariosByVideo(videoId);
        return ResponseEntity.ok(comentariosList);
    }
}
