package Trabajo.Ingenieria.Repositorios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import Trabajo.Ingenieria.Entidades.comentarios;
import Trabajo.Ingenieria.Entidades.videos;

import java.util.ArrayList;
import java.util.List;

@Repository
public class comentarioRepositorio {

    @Autowired
    private videosRepositorio videoRepositorio;

    @Autowired
    private comentarioCRUDrepositorio comentarioCRUDrepositorio;

    // Método para guardar un comentario
    public comentarios save(comentarios comentario) {
        return comentarioCRUDrepositorio.save(comentario);
    }

    // Método para encontrar un comentario por su ID
    public comentarios findById(Long id) {
        return comentarioCRUDrepositorio.findById(id).orElse(null);
    }

    public List<comentarios> findByVideo(Long videoId) {
        videos video = videoRepositorio.findById(videoId);
        if (video != null) {
            return comentarioCRUDrepositorio.findByVideo(video);
        }
        return new ArrayList<>(); // Devuelve una lista vacía si no se encuentra el video
    }

    // Método para eliminar un comentario por su ID
    public boolean deleteComentario(Long id) {
        if (comentarioCRUDrepositorio.existsById(id)) {
            comentarioCRUDrepositorio.deleteById(id);
            return true;
        }
        return false;
    }

}
