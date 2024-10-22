// NotificacionSuscripcion.java
package Trabajo.Ingenieria.DTOs;

import java.io.Serializable;

public class NotificacionSuscripcion implements Serializable {
    private Long idCanal;
    private String emailSuscriptor;

    public NotificacionSuscripcion(Long idCanal, String emailSuscriptor) {
        this.idCanal = idCanal;
        this.emailSuscriptor = emailSuscriptor;
    }

    // Getters
    public Long getIdCanal() {
        return idCanal;
    }

    public String getEmailSuscriptor() {
        return emailSuscriptor;
    }

    // Setters
    public void setIdCanal(Long idCanal) {
        this.idCanal = idCanal;
    }

    public void setEmailSuscriptor(String emailSuscriptor) {
        this.emailSuscriptor = emailSuscriptor;
    }
}