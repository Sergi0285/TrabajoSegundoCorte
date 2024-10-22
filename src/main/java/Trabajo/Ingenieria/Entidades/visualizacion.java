package Trabajo.Ingenieria.Entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class visualizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long idVisualizacion;

    @Column(name = "usuario_username", nullable = false) // Almacena el username directamente
    private String usuarioUsername;

    @ManyToOne
    @JoinColumn(name = "video_id") // Nombre de la columna en la tabla de videos
    private videos videos;

    // Getters y setters
    public String getUsuarioUsername() {
        return usuarioUsername;
    }

    public void setUsuarioUsername(String usuarioUsername) {
        this.usuarioUsername = usuarioUsername;
    }

    public videos getVideos() {
        return videos;
    }

    public void setVideos(videos videos) {
        this.videos = videos;
    }
}
