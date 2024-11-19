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

    // Relación ManyToOne con usuario que se suscribe
    @ManyToOne
    @JoinColumn(name = "Suscriptor_id")
    private usuario usuario; 

    // Relación ManyToOne con videos, representando el video específico al que se suscribe
    @ManyToOne
    @JoinColumn(name = "Canal_id")
    @JsonIgnore
    private videos video;

    // Relación ManyToOne con el usuario dueño del canal
    @ManyToOne
    @JoinColumn(name = "Canal_usuario_id") // Nuevo campo
    private usuario canalUsuario; // Relación directa al dueño del canal

    // Getters y Setters

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

    public usuario getCanalUsuario() {
        return canalUsuario;
    }

    public void setCanalUsuario(usuario canalUsuario) {
        this.canalUsuario = canalUsuario;
    }
}
