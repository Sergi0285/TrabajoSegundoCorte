package Trabajo.Ingenieria.Repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import Trabajo.Ingenieria.Entidades.comentarios;
import Trabajo.Ingenieria.Entidades.videos;

import java.util.List;

public interface comentarioCRUDrepositorio extends JpaRepository<comentarios, Long> {
    
    // Método para obtener todos los comentarios de un video específico
    List<comentarios> findByVideo(videos video);

}
