package Trabajo.Ingenieria.Repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Trabajo.Ingenieria.Entidades.suscripcion;

import java.util.List;
import java.util.Optional;

@Repository
public interface suscripcionRepositorio extends JpaRepository<suscripcion, Long> {
    
    // Cambiar VideoId por video_idVideo
    List<suscripcion> findByVideoIdVideo(Long idVideo);
    
    // Cambiar UsuarioId por usuario_id
    List<suscripcion> findByUsuarioId(Long id);
    
    // Ajustar los nombres de los campos
    Optional<suscripcion> findByUsuarioIdAndVideoIdVideo(Long id, Long idVideo);
    
    // Ajustar los nombres de los campos
    boolean existsByUsuarioIdAndVideoIdVideo(Long id, Long idVideo);
    
    // Cambiar VideoId por video_idVideo
    Long countByVideoIdVideo(Long idVideo);
    
    // Este está bien si usuario tiene un campo id
    Long countByUsuarioId(Long id);
}