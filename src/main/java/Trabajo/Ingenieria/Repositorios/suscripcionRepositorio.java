package Trabajo.Ingenieria.Repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Trabajo.Ingenieria.Entidades.suscripcion;

import java.util.List;
import java.util.Optional;

@Repository
public interface suscripcionRepositorio extends JpaRepository<suscripcion, Long> {
    
    List<suscripcion> findByVideoId(Long canalId);
    
    List<suscripcion> findByUsuarioId(Long usuarioId);
    
    Optional<suscripcion> findByUsuarioIdAndVideoId(Long usuarioId, Long canalId);
    
    boolean existsByUsuarioIdAndVideoId(Long usuarioId, Long canalId);
    
    Long countByVideoId(Long canalId);
    
    Long countByUsuarioId(Long usuarioId);
}