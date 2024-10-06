package Trabajo.Ingenieria.Servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import Trabajo.Ingenieria.Entidades.videos;
import Trabajo.Ingenieria.Repositorios.videosRepositorio;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.List;

@Service
public class videosServicio {
    
    private static final String VIDEO_DIRECTORY = "C:/Users/USUARIO/Desktop/videos/";  // Ruta donde se almacenarán los videos

    @Autowired
    private videosRepositorio videoRepository;

    public videos saveVideo(MultipartFile file, String alias, String titulo, String descripcion) throws IOException {
        // Guardar el archivo en el servidor
        Path videoPath = Paths.get(VIDEO_DIRECTORY, file.getOriginalFilename());
        Files.createDirectories(videoPath.getParent());
        Files.write(videoPath, file.getBytes());

        // Crear y guardar la entidad de video en la base de datos
        videos video = new videos();
        video.setAlias(alias);
        video.setTitulo(titulo);
        video.setDescripcion(descripcion);
        video.setUrl(videoPath.toString());
        video.setFechaSubida(new Date(System.currentTimeMillis()));
        
        return videoRepository.save(video);
    }

    public List<videos> getAllVideos() {
        return videoRepository.findAll();
    }
}
