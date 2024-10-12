package Trabajo.Ingenieria.Servicios;

import org.hibernate.mapping.Map;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import Trabajo.Ingenieria.Entidades.comentarios;
import Trabajo.Ingenieria.Entidades.usuario;
import Trabajo.Ingenieria.Entidades.videos;
import Trabajo.Ingenieria.Repositorios.comentarioRepositorio;

import java.util.List;

@Service
public class comentarioServicio {

    @Autowired
    private clienteServicio clienteServicio;

    @Autowired
    private videosServicio videosServicio;

    @Autowired
    private comentarioRepositorio comentarioRepositorio;

    public comentarios addComentario(comentarios comentario) {
        return comentarioRepositorio.save(comentario);
    }

    // Listener para eliminar un comentario
    @RabbitListener(queues = "comentario.cola.delete")
    public boolean deleteComentario(Long id) {
        return comentarioRepositorio.deleteComentario(id);
    }

    // Método para obtener todos los comentarios de un video
    public List<comentarios> getComentariosByVideo(Long videoId) {
        return comentarioRepositorio.findByVideo(videoId);
    }

    public comentarios editComentario(Long id, String nuevoComentario) {
        comentarios comentario = comentarioRepositorio.findById(id);
        if (comentario != null) {
            comentario.setComentario(nuevoComentario);
            return comentarioRepositorio.save(comentario);
        }
        return null; // o lanza una excepción si prefieres
    }
}