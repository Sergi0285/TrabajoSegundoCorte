package Trabajo.Ingenieria.Controladores;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import Trabajo.Ingenieria.Servicios.videosServicio;
import Trabajo.Ingenieria.Entidades.videos;

import java.util.List;

import java.io.IOException;

@RestController
@RequestMapping("/videos")
public class videosControlador {

    @Autowired
    private videosServicio videoService;

    @PostMapping("/upload")
    public ResponseEntity<videos> uploadVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam("alias") String alias,
            @RequestParam("titulo") String titulo,
            @RequestParam("descripcion") String descripcion) throws IOException {
        videos video = videoService.saveVideo(file, alias, titulo, descripcion);
        return ResponseEntity.ok(video);
    }

    @GetMapping("/Lista")
    public ResponseEntity<List<videos>> getAllVideos() {
        List<videos> videoList = videoService.getAllVideos();
        return ResponseEntity.ok(videoList);
    }
}

