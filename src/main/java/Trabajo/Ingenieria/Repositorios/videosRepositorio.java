package Trabajo.Ingenieria.Repositorios;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import Trabajo.Ingenieria.Entidades.categoria;
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

        // Obtener las categorías más vistas por un usuario
    public List<Object[]> findMostViewedCategoriesByUser(String username) {
        return video.findMostViewedCategoriesByUser(username);
    }

    // Encontrar videos no vistos por el usuario en una categoría
    public List<videos> findUnwatchedVideosByCategory(String username, categoria categoria) {
        return video.findUnwatchedVideosByCategory(username, categoria);
    }

    public List<videos> findByUsuarioId(Long usuarioId) {
        return video.findByUsuarioId(usuarioId);
    }
}
