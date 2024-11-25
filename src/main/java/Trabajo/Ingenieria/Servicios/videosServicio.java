package Trabajo.Ingenieria.Servicios;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import Trabajo.Ingenieria.DTOs.NotificacionNuevoVideo;
import Trabajo.Ingenieria.Entidades.categoria;
import Trabajo.Ingenieria.Entidades.usuario;
import Trabajo.Ingenieria.Entidades.videos;
import Trabajo.Ingenieria.Repositorios.suscripcionRepositorio;
import Trabajo.Ingenieria.Repositorios.videosRepositorio;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Base64;

@Service
public class videosServicio {

    private static final String VIDEO_DIRECTORY = "recursos/videos"; // Ruta donde se almacenarán los videos
    private static final String MINIATURAS_DIRECTORY = "recursos/miniaturas"; // Ruta donde se almacenaran las
                                                                              // miniaturas
    @Autowired
    private suscripcionRepositorio suscripcionRepo;

    @Autowired
    private videosRepositorio videoRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public List<videos> getRecomendaciones(String username) {
        // Obtener las categorías de los videos que ha visto el usuario
        List<Object[]> categoriasFrecuentes = videoRepository.findMostViewedCategoriesByUser(username);

        if (categoriasFrecuentes.isEmpty()) {
            // Si no hay videos vistos, retornar algunos videos aleatorios
            System.out.println("No se encontraron categorías frecuentes para el usuario: " + username);
            return videoRepository.findUnwatchedVideosByCategory(username, categoria.Chisme).subList(0, 5);
        }

        // Obtener la categoría más vista
        categoria categoriaFavorita = (categoria) categoriasFrecuentes.get(0)[0];
        System.out.println("Categoría favorita: " + categoriaFavorita);

        // Encontrar videos no vistos por el usuario en esa categoría
        List<videos> recomendaciones = videoRepository.findUnwatchedVideosByCategory(username, categoriaFavorita);

        // Si no se encontraron videos recomendados, registrar ese detalle
        if (recomendaciones.isEmpty()) {
            System.out.println(
                    "No se encontraron videos recomendados para el usuario en la categoría: " + categoriaFavorita);
        }

        // Retornar hasta 5 recomendaciones
        return recomendaciones.size() > 5 ? recomendaciones.subList(0, 5) : recomendaciones;
    }

    public videos saveVideo(MultipartFile file, MultipartFile imageFile, usuario user, String titulo,
            String descripcion) throws IOException {
        System.out.println("Entrando en saveVideo...");
        System.out.println("Título del video: " + titulo);

        videos video = new videos();
        video.setUsuario(user);
        video.setTitulo(titulo);
        video.setDescripcion(descripcion);
        video.setFechaSubida(new Date(System.currentTimeMillis()));
        videoRepository.save(video);

        // Usar el ID del video para crear un nombre de archivo único ID + nombre
        // original del archivo
        @SuppressWarnings("null")
        String cleanFilename = file.getOriginalFilename().replaceAll("\\s+", "_");
        String newFilename = video.getIdVideo() + "_" + cleanFilename;
        Path videoPath = Paths.get(System.getProperty("user.dir"), VIDEO_DIRECTORY, newFilename);
        video.setUrl(videoPath.toString());

        @SuppressWarnings("null")
        String extension = imageFile.getOriginalFilename().substring(imageFile.getOriginalFilename().lastIndexOf('.')); // Obtiene
                                                                                                                        // la
                                                                                                                        // extensión
                                                                                                                        // del
                                                                                                                        // archivo
        String nombreMiniatura = video.getIdVideo() + "_miniatura" + extension;
        Path urlMiniatura = Paths.get(System.getProperty("user.dir"), MINIATURAS_DIRECTORY, nombreMiniatura);

        video.setMiniatura(urlMiniatura.toString());

        Map<String, String> mapa = new HashMap<>();
        mapa.put("path", videoPath.toString());
        mapa.put("fileData", Base64.getEncoder().encodeToString(file.getBytes()));
        mapa.put("miniatura", Base64.getEncoder().encodeToString(imageFile.getBytes()));
        mapa.put("urlminiatura", urlMiniatura.toString());
        rabbitTemplate.convertAndSend("video.cola", mapa);
        rabbitTemplate.convertAndSend("miniatura.cola", mapa);
        ;
        // Agregar lógica de notificación
        List<String> correosSuscriptores = suscripcionRepo.findByVideoIdVideo(video.getIdVideo())
                .stream()
                .map(suscripcion -> suscripcion.getUsuario().getEmail())
                .collect(Collectors.toList());
        // Agregar el log para verificar los correos encontrados
        System.out.println("Correos de suscriptores: " + correosSuscriptores);
        if (!correosSuscriptores.isEmpty()) {
            NotificacionNuevoVideo notificacion = new NotificacionNuevoVideo(
                    video.getIdVideo(),
                    video.getTitulo(),
                    String.join(",", correosSuscriptores));
            System.out.println("Publicando notificación a nuevoVideo.cola...");
            rabbitTemplate.convertAndSend("notificacionesExchange", "nuevoVideo.cola", notificacion);
            System.out.println("Notificación publicada: " + notificacion);

        }

        return videoRepository.save(video);
    }

    public void guardarMiniatura(String files, String imgPath) throws IOException {
        Path url = Paths.get(imgPath);
        byte[] file = Base64.getDecoder().decode(files);
        Files.write(url, file);
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

    public ResponseEntity<Resource> streamImage(Long id) throws IOException {
        videos video = videoRepository.findById(id);
        Path videoPath = Paths.get(video.getMiniatura());

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

    public List<videos> findByUsuarioId(Long usuarioId) {
        return videoRepository.findByUsuarioId(usuarioId);
    }

}