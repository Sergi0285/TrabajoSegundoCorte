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

    // Cambiamos la relación para que sea con el usuario del canal en lugar del video
    @ManyToOne
    @JoinColumn(name = "Suscriptor_id")
    private usuario suscriptor;

    @ManyToOne
    @JoinColumn(name = "Canal_id")
    @JsonIgnore
    private usuario canal;  // Ahora nos suscribimos al usuario (canal) en lugar del video

    // Getters y setters actualizados
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

    public usuario getSuscriptor() {
        return suscriptor;
    }

    public void setSuscriptor(usuario suscriptor) {
        this.suscriptor = suscriptor;
    }

    public usuario getCanal() {
        return canal;
    }

    public void setCanal(usuario canal) {
        this.canal = canal;
    }
}