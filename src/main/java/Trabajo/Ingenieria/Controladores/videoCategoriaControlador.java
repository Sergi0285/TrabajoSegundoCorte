package Trabajo.Ingenieria.Controladores;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import Trabajo.Ingenieria.Entidades.categoria;
import Trabajo.Ingenieria.Entidades.videoCategoria;
import Trabajo.Ingenieria.Entidades.videos;
import Trabajo.Ingenieria.Servicios.videoCategoriaServicio;
import Trabajo.Ingenieria.Servicios.videosServicio;

@RestController
@RequestMapping("/categoria")
public class videoCategoriaControlador {
    @Autowired
    private videoCategoriaServicio videoCategoriaServicio;
    @Autowired
    private videosServicio videoServicio;

    @PostMapping("/add")
    public ResponseEntity<List<videoCategoria>> addVideoCategoria(@RequestParam("Id") Long videoId, @RequestParam("categorias") List<categoria> categorias) {
        
        List<videoCategoria> savedVideoCategorias = new ArrayList<>();
        
        // Verifica si el video existe
        videos video = videoServicio.findById(videoId);
        if (video == null) {
            System.out.println("No existe");
            return ResponseEntity.badRequest().body(null); // Manejo de error si el video no existe
        }
        
        for (categoria cat : categorias) {
            videoCategoria videoCategoria = new videoCategoria();
            videoCategoria.setVideo(video); // Asumiendo que tienes un método adecuado para establecer el video
            videoCategoria.setCategoria(cat);
            
            // Guarda la categoría del video
            videoCategoria savedVideoCategoria = videoCategoriaServicio.save(videoCategoria);
            savedVideoCategorias.add(savedVideoCategoria);
        }
        
        return ResponseEntity.ok(savedVideoCategorias);
    }

}
