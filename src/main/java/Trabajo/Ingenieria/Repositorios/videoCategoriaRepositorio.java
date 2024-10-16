package Trabajo.Ingenieria.Repositorios;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import Trabajo.Ingenieria.Entidades.videoCategoria;

@Repository
public class videoCategoriaRepositorio {
    @Autowired
    private videoCategoriaCRUDrepositorio video;

    public videoCategoria save(videoCategoria v){
        return video.save(v);
    }

    public List<videoCategoria> obtenerCategoriasPorVideo(Long idVideo) {
        return video.findByVideo_IdVideo(idVideo);
    }
}
