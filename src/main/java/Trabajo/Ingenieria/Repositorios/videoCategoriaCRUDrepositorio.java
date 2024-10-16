package Trabajo.Ingenieria.Repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import Trabajo.Ingenieria.Entidades.videoCategoria;

public interface videoCategoriaCRUDrepositorio extends JpaRepository<videoCategoria, Long> {

    List<videoCategoria> findByVideo_IdVideo(Long idVideo);
}
