package Trabajo.Ingenieria.Repositorios;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import Trabajo.Ingenieria.Entidades.videos;

@Repository
public class videosRepositorio {

    @Autowired
    private videosCRUDrepositorio video;

    public videos save(videos v){
        return video.save(v);
    }

    public videos findById(Long id){
        return video.findById(id).orElse(null);
    }

    public List<videos> findAll(){
        return video.findAll();
    }
}
