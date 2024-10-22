package Trabajo.Ingenieria.Repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Trabajo.Ingenieria.Entidades.suscripcion;


@Repository
public interface suscripcionRepositorio extends JpaRepository<suscripcion, Long> {
    List<suscripcion> findByCanalId(Long canalId);
    List<suscripcion> findBySuscriptorId(Long suscriptorId);
    Optional<suscripcion> findBySuscriptorIdAndCanalId(Long suscriptorId, Long canalId);
    boolean existsBySuscriptorIdAndCanalId(Long suscriptorId, Long canalId);
    Long countByCanalId(Long canalId);
    Long countBySuscriptorId(Long suscriptorId);
}