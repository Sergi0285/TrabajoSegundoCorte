package Trabajo.Ingenieria.Entidades;

import java.sql.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
public class videos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long idVideo;

    String titulo;
    String descripcion;
    String url;

    @Temporal(TemporalType.DATE)
    Date fechaSubida;

    @OneToMany(mappedBy = "video")
    @JsonIgnore
    List<videoCategoria> videoCategorias;

    String miniatura;

    // Nueva relación con usuario
    @ManyToOne
    @JoinColumn(name = "usuario_id") // Nombre de la columna en la tabla de videos
    private usuario usuario; // Usuario que subió el video

    // Getters y Setters
    public Long getIdVideo() {
        return idVideo;
    }

    public void setIdVideo(Long idVideo) {
        this.idVideo = idVideo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getFechaSubida() {
        return fechaSubida;
    }

    public void setFechaSubida(Date fechaSubida) {
        this.fechaSubida = fechaSubida;
    }

    public List<videoCategoria> getVideoCategorias() {
        return videoCategorias;
    }

    public void setVideoCategorias(List<videoCategoria> videoCategorias) {
        this.videoCategorias = videoCategorias;
    }

    public String getMiniatura() {
        return miniatura;
    }

    public void setMiniatura(String miniatura) {
        this.miniatura = miniatura;
    }

    public usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(usuario usuario) {
        this.usuario = usuario;
    }

}
