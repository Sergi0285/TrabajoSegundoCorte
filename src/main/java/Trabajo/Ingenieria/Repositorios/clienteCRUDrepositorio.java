package Trabajo.Ingenieria.Repositorios;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import Trabajo.Ingenieria.Entidades.usuario;

public interface clienteCRUDrepositorio extends CrudRepository <usuario,Long>{

    usuario findByUsername(String username);

    Optional<usuario> findById(Long id);
}
