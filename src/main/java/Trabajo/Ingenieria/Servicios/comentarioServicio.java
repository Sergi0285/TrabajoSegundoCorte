package Trabajo.Ingenieria.Servicios;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import Trabajo.Ingenieria.Entidades.comentarios;
import Trabajo.Ingenieria.Repositorios.comentarioRepositorio;

import java.util.List;

@Service
public class comentarioServicio {

    @Autowired
    private comentarioRepositorio comentarioRepositorio;

    // Listener para agregar un comentario
    @RabbitListener(queues = "comentario.cola.add")
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
}