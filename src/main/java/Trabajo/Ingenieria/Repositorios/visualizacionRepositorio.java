package Trabajo.Ingenieria.Repositorios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import Trabajo.Ingenieria.Entidades.visualizacion;

@Repository
public class visualizacionRepositorio {
    
    @Autowired
    private visualizacionCRUDRepositorio visualizacion;

    public visualizacion save(visualizacion v){
        return visualizacion.save(v);
    }

    public visualizacion findById(Long id){
        return visualizacion.findById(id).orElse(null);
    }
}
