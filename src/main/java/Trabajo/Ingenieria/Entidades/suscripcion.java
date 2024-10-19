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
public class suscripcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSuscripcion;
    private LocalDateTime fechaSuscripcion;

    // Relación ManyToOne con usuario
    @ManyToOne
    @JoinColumn(name = "Suscriptor_id")
    private usuario usuario; 

    // Relación ManyToOne con videos
    @ManyToOne
    @JoinColumn(name = "Canal_id")
    @JsonIgnore
    private videos video;

    public Long getIdSuscripcion() {
        return idSuscripcion;
    }

    public void setIdSuscripcion(Long idSuscripcion) {
        this.idSuscripcion = idSuscripcion;
    }

    public LocalDateTime getFechaSuscripcion() {
        return fechaSuscripcion;
    }

    public void setFechaSuscripcion(LocalDateTime fechaSuscripcion) {
        this.fechaSuscripcion = fechaSuscripcion;
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
