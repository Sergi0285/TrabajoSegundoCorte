package Trabajo.Ingenieria.Controladores;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import Trabajo.Ingenieria.DTOs.NotificacionNuevoVideo;
import Trabajo.Ingenieria.Entidades.categoria;
import Trabajo.Ingenieria.Entidades.usuario;
import Trabajo.Ingenieria.Entidades.videos;
import Trabajo.Ingenieria.Servicios.NotificationService;
import Trabajo.Ingenieria.Servicios.clienteServicio;
import Trabajo.Ingenieria.Servicios.videosServicio;

@RestController
@RequestMapping("/videos")
public class videosControlador {

    @Autowired
    private videosServicio videoService; // Nota: "videoService" es el nombre correcto

    @Autowired
    private clienteServicio cliente;
    @Autowired
    private NotificationService notificationService;

    @PostMapping("/upload")
    public ResponseEntity<Long> uploadVideo(@RequestParam("file") MultipartFile file, @RequestParam("image") MultipartFile imageFile,
                                            @RequestParam("alias") String alias, 
                                            @RequestParam("titulo") String titulo, @RequestParam("descripcion") String descripcion) throws IOException {
        usuario user = cliente.findByUsername(alias);
        videos video = videoService.saveVideo(file, imageFile, user, titulo, descripcion);

        return ResponseEntity.ok(video.getIdVideo());
    }

    // Nuevo endpoint para manejar la notificación de nuevos videos
    @PostMapping("/notificar")
    public ResponseEntity<?> notificarNuevoVideo(@RequestBody NotificacionNuevoVideo notificacion) {
        notificationService.enviarNotificacionNuevoVideo(
                notificacion.getIdCanal(),
                notificacion.getTituloVideo(),
                notificacion.getEmailSuscriptores());
        return ResponseEntity.ok("Notificación de nuevo video enviada");
    }

    @GetMapping("/Lista")
    public List<videos> getAllVideos() {
        List<videos> videoList = videoService.getAllVideos();
        return videoList;
    }

    @GetMapping("/obtenerInfo")
    public ResponseEntity<videos> obtenerInfo(Long id) {
        return ResponseEntity.ok(videoService.findById(id));
    }

    @GetMapping("/ver")
    public ResponseEntity<Resource> streamVideo(@RequestParam("id") Long id) throws IOException {
        return videoService.streamVideo(id);
    }

    @GetMapping("/miniatura")
    public ResponseEntity<Resource> streamImage(@RequestParam("id") Long id) throws IOException {
        return videoService.streamImage(id);
    }

    @GetMapping("/categorias")
    public List<categoria> getCategorias() {
        return Arrays.asList(categoria.values());
    }

    @GetMapping("/randomVideos")
    public List<videos> getRandomVideos() {
        List<videos> todosLosVideos = videoService.getAllVideos();

        // Mezclar los videos para obtener un orden aleatorio
        Collections.shuffle(todosLosVideos);

        // Retornar solo 6 videos al azar
        return todosLosVideos.stream().limit(6).collect(Collectors.toList());
    }

    @GetMapping("/recomendaciones")
    public ResponseEntity<List<videos>> getRecomendaciones(@RequestParam String username) {
        // Aquí debes usar videoService, no videosServicio
        List<videos> recomendaciones = videoService.getRecomendaciones(username);
        return new ResponseEntity<>(recomendaciones, HttpStatus.OK);
    }
}