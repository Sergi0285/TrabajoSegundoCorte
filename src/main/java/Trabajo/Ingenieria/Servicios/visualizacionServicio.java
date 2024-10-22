package Trabajo.Ingenieria.Servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import Trabajo.Ingenieria.Entidades.visualizacion;
import Trabajo.Ingenieria.Entidades.usuario;
import Trabajo.Ingenieria.Repositorios.visualizacionRepositorio;
import Trabajo.Ingenieria.Repositorios.clienteRepositorio;
import Trabajo.Ingenieria.Repositorios.videosRepositorio;


@Service
public class visualizacionServicio {

    @Autowired
    private clienteRepositorio clienteRepo;

    @Autowired
    private videosRepositorio videosRepo;

    @Autowired
    private visualizacionRepositorio visualizacionRepo;

    public void guardarVisualizacion(String username, Long videoid) throws Exception {
        usuario user = clienteRepo.findByUsername(username);
        if (user == null) {
            throw new Exception("Usuario no encontrado");
        }

        visualizacion nuevaVisualizacion = new visualizacion();
        nuevaVisualizacion.setUsuarioUsername(user.getUsername());
        nuevaVisualizacion.setVideos(videosRepo.findById(videoid));
        
        visualizacionRepo.save(nuevaVisualizacion);
    }
}