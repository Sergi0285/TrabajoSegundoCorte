package Trabajo.Ingenieria.Entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class videoCategoria {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Long idVideoCategoria;

    @ManyToOne
    @JoinColumn(name = "idVideo")
    videos video;

    @Enumerated(EnumType.STRING) 
    categoria categoria;

    public Long getIdVideoCategoria() {
        return idVideoCategoria;
    }

    public videos getVideo() {
        return video;
    }

    public categoria getCategoria() {
        return categoria;
    }

    public void setIdVideoCategoria(Long idVideoCategoria) {
        this.idVideoCategoria = idVideoCategoria;
    }

    public void setVideo(videos video) {
        this.video = video;
    }

    public void setCategoria(categoria categoria) {
        this.categoria = categoria;
    }
    
}
