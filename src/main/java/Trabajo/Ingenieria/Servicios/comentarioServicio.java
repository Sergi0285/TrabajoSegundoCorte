package Trabajo.Ingenieria.Servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import Trabajo.Ingenieria.Entidades.comentarios;
import Trabajo.Ingenieria.Repositorios.comentarioRepositorio;

import java.util.List;

@Service
public class comentarioServicio {

    @Autowired
    private comentarioRepositorio comentarioRepositorio;

    public comentarios addComentario(comentarios comentario) {
        return comentarioRepositorio.save(comentario);
    }

    public comentarios editComentario(Long id, String nuevoComentario, String username) {
        comentarios comentario = comentarioRepositorio.findById(id);
        if (comentario != null && comentario.getUsuario().getUsername().equals(username)) {
            comentario.setComentario(nuevoComentario);
            return comentarioRepositorio.save(comentario);
        }
        return null; // o lanza una excepción si prefieres
    }
    
    public boolean deleteComentario(Long id, String username) {
        comentarios comentario = comentarioRepositorio.findById(id);
        if (comentario != null && comentario.getUsuario().getUsername().equals(username)) {
            comentarioRepositorio.deleteComentario(id);
            return true;
        }
        return false; // o lanza una excepción si prefieres
    }
        
    
    // Método para obtener todos los comentarios de un video
    public List<comentarios> getComentariosByVideo(Long videoId) {
        return comentarioRepositorio.findByVideo(videoId);
    }
}