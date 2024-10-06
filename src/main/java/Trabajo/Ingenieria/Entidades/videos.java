package Trabajo.Ingenieria.Entidades;

import java.sql.Date;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
public class videos {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Long idVideo;
    
    String alias;
    String titulo;
    String descripcion;
    String url;
    
    @Temporal(TemporalType.DATE)
    Date fechaSubida;
    
    @OneToMany(mappedBy = "video")
    List<videoCategoria> videoCategorias;

    String miniatura;

    public Long getIdVideo() {
        return idVideo;
    }

    public void setIdVideo(Long idVideo) {
        this.idVideo = idVideo;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
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
}
