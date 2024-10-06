package Trabajo.Ingenieria.Repositorios;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import Trabajo.Ingenieria.Entidades.usuario;

public interface usuarioRepositorio extends JpaRepository<usuario,Long> {
    Optional<usuario> findByUsername(String username); 
    boolean existsByUsername(String username);
}
