package Trabajo.Ingenieria.Rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Map;

import Trabajo.Ingenieria.Entidades.comentarios;
import Trabajo.Ingenieria.Entidades.usuario;
import Trabajo.Ingenieria.Entidades.videos;
import Trabajo.Ingenieria.Servicios.videosServicio;
import Trabajo.Ingenieria.Servicios.clienteServicio;
import Trabajo.Ingenieria.Servicios.comentarioServicio;

import java.io.IOException;


@Service
public class RabbitMQConsumers {

    @Autowired
    private clienteServicio clienteServicio;

    @Autowired
    private videosServicio videoService;

    @Autowired
    private comentarioServicio comentarioServicio; // Instancia inyectada

    // Listener para almacenar el video
    @RabbitListener(queues = "video.cola")
    public void storeVideo(@Payload Map<String, String> message) throws IOException, InterruptedException {
        String videoPath = (String) message.get("path");
        String file = (String) message.get("fileData");
        videoService.guardarVideo(videoPath, file);
    }
    
    @RabbitListener(queues = "miniatura.cola")
    public void storeMiniatura(@Payload Map<String, String> message) throws IOException, InterruptedException {
        String imgPath = (String) message.get("urlminiatura");
        String file = (String) message.get("miniatura");
        videoService.guardarMiniatura(file, imgPath);
    }

    // Listener para almacenar comentarios
    @RabbitListener(queues = "comentario.cola.add")
    public void addComentario(@Payload Map<String, String> message) {
        comentarios comentario = new comentarios();
        comentario.setComentario(message.get("comentario"));
        comentario.setFechaComentario(java.sql.Date.valueOf(message.get("fechaComentario")));

        // Obtener el video a partir del ID y asignarlo al comentario
        Long videoId = Long.valueOf(message.get("video"));
        videos video = videoService.findById(videoId);
        comentario.setVideo(video);

        // Obtener el usuario por nombre de usuario
        String username = message.get("usuario"); // Suponiendo que el nombre de usuario viene en el mensaje
        usuario user = clienteServicio.findByUsername(username);
        if (user != null) { // Verificar si se encontró el usuario
            comentario.setUsuario(user); // Asignar el usuario encontrado
        } else {
            // Manejo de errores si no se encuentra el usuario
            System.err.println("Usuario no encontrado: " + username);
        }

        comentarioServicio.addComentario(comentario);
    }

    // Listener para eliminar un comentario
    @RabbitListener(queues = "comentario.cola.delete")
    public void deleteComentario(@Payload Map<String, String> message) {
        Long idComentario = Long.valueOf(message.get("idComentario"));
        comentarioServicio.deleteComentario(idComentario);
    }
}