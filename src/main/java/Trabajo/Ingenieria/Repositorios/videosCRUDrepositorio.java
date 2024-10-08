package Trabajo.Ingenieria.Repositorios;


import org.springframework.data.jpa.repository.JpaRepository;
import Trabajo.Ingenieria.Entidades.videos;


public interface videosCRUDRepositorio extends JpaRepository<videos, Long> {
    
}
