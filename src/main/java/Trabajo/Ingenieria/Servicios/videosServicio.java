package Trabajo.Ingenieria.Servicios;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;


import Trabajo.Ingenieria.Entidades.usuario;
import Trabajo.Ingenieria.Entidades.videos;
import Trabajo.Ingenieria.Repositorios.videosRepositorio;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Base64;

@Service
public class videosServicio {
    
    private static final String VIDEO_DIRECTORY = "recursos/videos";  // Ruta donde se almacenarán los videos

    @Autowired
    private videosRepositorio videoRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public videos saveVideo(MultipartFile file, usuario user, String titulo, String descripcion) throws IOException {
        videos video = new videos();
        video.setUsuario(user);
        video.setTitulo(titulo);
        video.setDescripcion(descripcion);
        video.setFechaSubida(new Date(System.currentTimeMillis()));
        videoRepository.save(video);

        // Usar el ID del video para crear un nombre de archivo // único ID + nombre original del archivo
        String newFilename = video.getIdVideo() + "_" + file.getOriginalFilename(); 
        Path videoPath = Paths.get(System.getProperty("user.dir"), VIDEO_DIRECTORY, newFilename);
        video.setUrl(videoPath.toString());

        Map<String, String> mapa = new HashMap<>();
        mapa.put("path", videoPath.toString());
        mapa.put("fileData", Base64.getEncoder().encodeToString(file.getBytes()));
        mapa.put("id", String.valueOf(video.getIdVideo()));
        rabbitTemplate.convertAndSend("video.cola", mapa);
        
        return videoRepository.save(video);
    }

    public videos findById(Long id) {
        return videoRepository.findById(id);
    }

    public List<videos> getAllVideos() {
        return videoRepository.findAll();
    }

    public ResponseEntity<Resource> streamVideo(Long id) throws IOException {
        videos video = videoRepository.findById(id);
        Path videoPath = Paths.get(video.getUrl());

        Resource videoResource = new UrlResource(videoPath.toUri());
        return ResponseEntity.ok()
                .contentType(MediaTypeFactory.getMediaType(videoResource).orElse(MediaType.APPLICATION_OCTET_STREAM))
                .body(videoResource);
    }

    public void guardarVideo(String videoPath, String files) throws IOException {
        Path url = Paths.get(videoPath);
        byte[] file = Base64.getDecoder().decode(files);
        Files.write(url, file);
    }
}
