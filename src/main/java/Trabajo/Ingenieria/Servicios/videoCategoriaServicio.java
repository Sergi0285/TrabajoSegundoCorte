package Trabajo.Ingenieria.Servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Trabajo.Ingenieria.Entidades.videoCategoria;
import Trabajo.Ingenieria.Repositorios.videoCategoriaRepositorio;

@Service
public class videoCategoriaServicio {
    @Autowired
    private videoCategoriaRepositorio video;

    public videoCategoria save(videoCategoria v){
        return video.save(v);
    }
}
