package Trabajo.Ingenieria.DTOs;

import java.io.Serializable;

public class NotificacionNuevoVideo implements Serializable {
    private Long idCanal;
    private String tituloVideo;
    private String emailSuscriptores;

    public NotificacionNuevoVideo(Long idCanal, String tituloVideo, String emailSuscriptores) {
        this.idCanal = idCanal;
        this.tituloVideo = tituloVideo;
        this.emailSuscriptores = emailSuscriptores;
    }

    // Getters
    public Long getIdCanal() {
        return idCanal;
    }

    public String getTituloVideo() {
        return tituloVideo;
    }

    public String getEmailSuscriptores() {
        return emailSuscriptores;
    }

    // Setters
    public void setIdCanal(Long idCanal) {
        this.idCanal = idCanal;
    }

    public void setTituloVideo(String tituloVideo) {
        this.tituloVideo = tituloVideo;
    }

    public void setEmailSuscriptores(String emailSuscriptores) {
        this.emailSuscriptores = emailSuscriptores;
    }
}