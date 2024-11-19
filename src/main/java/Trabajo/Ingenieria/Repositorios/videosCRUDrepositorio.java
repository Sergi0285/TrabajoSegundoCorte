package Trabajo.Ingenieria.Repositorios;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import Trabajo.Ingenieria.Entidades.categoria;
import Trabajo.Ingenieria.Entidades.videos;


public interface videosCRUDrepositorio extends JpaRepository<videos, Long> {

    @Query("SELECT vc.categoria, COUNT(vc.categoria) as frecuencia "
        + "FROM visualizacion vis "
        + "JOIN vis.videos v "
        + "JOIN v.videoCategorias vc "
        + "WHERE vis.usuarioUsername = :username "
        + "GROUP BY vc.categoria "
        + "ORDER BY frecuencia DESC")
    List<Object[]> findMostViewedCategoriesByUser(@Param("username") String username);

    @Query("SELECT v FROM videos v "
        + "JOIN v.videoCategorias vc "
        + "WHERE vc.categoria = :categoria "
        + "AND v.idVideo NOT IN "
        + "(SELECT vis.videos.idVideo FROM visualizacion vis WHERE vis.usuarioUsername = :username)")
    List<videos> findUnwatchedVideosByCategory(@Param("username") String username, @Param("categoria") categoria categoria);

    @Query("SELECT v FROM videos v WHERE v.usuario.id = :usuarioId")
    List<videos> findByUsuarioId(@Param("usuarioId") Long usuarioId);
}
