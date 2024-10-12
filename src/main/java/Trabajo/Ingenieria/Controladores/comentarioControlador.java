package Trabajo.Ingenieria.Controladores;

import java.util.Map;
import java.util.List;
import java.time.LocalDateTime;

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
    private videosServicio videoServicio;

    @PostMapping("/agregar")
    public ResponseEntity<String> agregarComentario(@RequestBody Map<String, String> request) {
        String contenidoComentario = request.get("comentario");
        Long videoId = Long.parseLong(request.get("videoId"));
        String username = request.get("username");

        // Buscar el video por su ID
        videos video = videoServicio.findById(videoId);
        if (video == null) {
            return new ResponseEntity<>("Video no encontrado", HttpStatus.NOT_FOUND);
        }

        // Buscar el usuario por su nombre de usuario
        usuario user = clienteServicio.findByUsername(username);
        if (user == null) {
            return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
        }

        // Crear el comentario y asociar el video y el usuario
        comentarios comentario = new comentarios();
        comentario.setComentario(contenidoComentario);
        comentario.setFechaComentario(LocalDateTime.now()); // Establecer la fecha y hora actual
        comentario.setVideo(video);
        comentario.setUsuario(user);

        // Guardar el comentario
        comentarioServicio.addComentario(comentario);

        return new ResponseEntity<>("Comentario agregado exitosamente", HttpStatus.CREATED);
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

    // En comentarioControlador.java
    @PutMapping("/editar/{id}")
    public ResponseEntity<String> editarComentario(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String nuevoComentario = request.get("comentario");

        comentarios comentarioActualizado = comentarioServicio.editComentario(id, nuevoComentario);
        if (comentarioActualizado != null) {
            return ResponseEntity.ok("Comentario actualizado exitosamente");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
