package Trabajo.Ingenieria.Repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import Trabajo.Ingenieria.Entidades.visualizacion;

public interface visualizacionCRUDRepositorio extends JpaRepository<visualizacion, Long> {
}