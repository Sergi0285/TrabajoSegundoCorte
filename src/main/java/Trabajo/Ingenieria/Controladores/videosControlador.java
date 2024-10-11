package Trabajo.Ingenieria.Controladores;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import Trabajo.Ingenieria.Servicios.clienteServicio;
import Trabajo.Ingenieria.Servicios.videosServicio;
import Trabajo.Ingenieria.Entidades.categoria;
import Trabajo.Ingenieria.Entidades.usuario;
import Trabajo.Ingenieria.Entidades.videos;

import java.util.Arrays;
import java.util.List;
import java.io.IOException;

@RestController
@RequestMapping("/videos")
public class videosControlador {

    @Autowired
    private videosServicio videoService;
    @Autowired
    private clienteServicio cliente;

    @PostMapping("/upload")
    public ResponseEntity<Long> uploadVideo(@RequestParam("file") MultipartFile file, @RequestParam("image") MultipartFile imageFile,
    @RequestParam("alias") String alias, 
    @RequestParam("titulo") String titulo, @RequestParam("descripcion") String descripcion) throws IOException {
        usuario user = cliente.findByUsername(alias);
        videos video = videoService.saveVideo(file, imageFile ,user, titulo, descripcion);
        return ResponseEntity.ok(video.getIdVideo());
    }

    @GetMapping("/Lista")
    public List<videos> getAllVideos() {
        List<videos> videoList = videoService.getAllVideos();
        return videoList;
    }

    @GetMapping("/obtenerInfo")
    public ResponseEntity<videos> obtenerInfo(Long id){
        return ResponseEntity.ok(videoService.findById(id));
    }

    @GetMapping("/ver")
    public ResponseEntity<Resource> streamVideo(@RequestParam("id") Long id) throws IOException {
        return videoService.streamVideo(id);
    }

    @GetMapping("/categorias")
    public List<categoria> getCategorias() {
        return Arrays.asList(categoria.values());
    }
}