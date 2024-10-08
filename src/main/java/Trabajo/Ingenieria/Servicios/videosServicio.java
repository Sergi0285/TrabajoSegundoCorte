package Trabajo.Ingenieria.Servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import Trabajo.Ingenieria.Entidades.usuario;
import Trabajo.Ingenieria.Entidades.videos;
import Trabajo.Ingenieria.Repositorios.videosRepositorio;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.List;

@Service
public class videosServicio {
    
    private static final String VIDEO_DIRECTORY = "recursos/videos";  // Ruta donde se almacenarán los videos

    @Autowired
    private videosRepositorio videoRepository;

    public videos saveVideo(MultipartFile file, usuario user, String titulo, String descripcion) throws IOException {
        // Crear y guardar la entidad de video en la base de datos primero para obtener el ID
        videos video = new videos();
        video.setUsuario(user);
        video.setTitulo(titulo);
        video.setDescripcion(descripcion);
        video.setFechaSubida(new Date(System.currentTimeMillis()));
        
        // Guardar el video en la base de datos (sin URL por ahora)
        video = videoRepository.save(video);
        
        // Usar el ID del video para crear un nombre de archivo único
        String originalFilename = file.getOriginalFilename();
        String newFilename = video.getIdVideo() + "_" + originalFilename; // ID + nombre original del archivo
        Path videoPath = Paths.get(System.getProperty("user.dir"), VIDEO_DIRECTORY, newFilename);
        
        // Crear directorios si no existen
        Files.createDirectories(videoPath.getParent());
        
        // Guardar el archivo en el servidor
        Files.write(videoPath, file.getBytes());

        // Actualizar la URL del video en la entidad
        video.setUrl(videoPath.toString());

        // Guardar de nuevo la entidad de video con la URL actualizada
        return videoRepository.save(video);
    }

    public videos findById(Long id) {
        return videoRepository.findById(id);
    }

    public List<videos> getAllVideos() {
        return videoRepository.findAll();
    }
}
