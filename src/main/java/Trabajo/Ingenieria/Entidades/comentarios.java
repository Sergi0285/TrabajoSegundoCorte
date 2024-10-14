package Trabajo.Ingenieria.Entidades;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;


@Entity
public class comentarios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idComentario;

    private String comentario;

    private LocalDateTime fechaComentario;

    // Relación ManyToOne con usuario
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private usuario usuario; 

    // Relación ManyToOne con videos
    @ManyToOne
    @JoinColumn(name = "video_id")
    @JsonIgnore
    private videos video; 

    // Getters y Setters
    public Long getIdComentario() {
        return idComentario;
    }

    public void setIdComentario(Long idComentario) {
        this.idComentario = idComentario;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDateTime getFechaComentario() {
        return fechaComentario;
    }

    public void setFechaComentario(LocalDateTime fechaComentario) {
        this.fechaComentario = fechaComentario;
    }

    public usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(usuario usuario) {
        this.usuario = usuario;
    }

    public videos getVideo() {
        return video;
    }

    public void setVideo(videos video) {
        this.video = video;
    }
}
