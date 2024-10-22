package Trabajo.Ingenieria.Rabbitmq;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import Trabajo.Ingenieria.DTOs.NotificacionNuevoVideo;
import Trabajo.Ingenieria.DTOs.NotificacionSuscripcion;
import Trabajo.Ingenieria.Entidades.comentarios;
import Trabajo.Ingenieria.Entidades.usuario;
import Trabajo.Ingenieria.Entidades.videos;
import Trabajo.Ingenieria.Servicios.clienteServicio;
import Trabajo.Ingenieria.Servicios.comentarioServicio;
import Trabajo.Ingenieria.Servicios.videosServicio;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.MessagingException;


@Service
public class RabbitMQConsumers {

    @Autowired
    private clienteServicio clienteServicio;

    @Autowired
    private videosServicio videoService;

    @Autowired
    private comentarioServicio comentarioServicio; // Instancia inyectada
    @Autowired
    private JavaMailSender javaMailSender;

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
     @RabbitListener(queues = "suscripcion.cola")
    public void recibirSuscripcion(NotificacionSuscripcion notificacion) {
        // Lógica para enviar el correo al suscriptor
        enviarCorreo(notificacion.getEmailSuscriptor(), "Te has suscrito a un canal",
                     "Te has suscrito al canal con ID: " + notificacion.getIdCanal());
    }

    @RabbitListener(queues = "nuevoVideo.cola")
    public void recibirNuevoVideo(NotificacionNuevoVideo notificacion) {
        // Lógica para enviar el correo a los suscriptores del canal
        enviarCorreo(notificacion.getEmailSuscriptores(), "Nuevo video disponible",
                     "Se ha subido un nuevo video titulado: " + notificacion.getTituloVideo());
    }

    private void enviarCorreo(String destinatario, String asunto, String contenido) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(contenido, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace(); // Manejo de errores para depuración
        }
    }


    @RabbitListener(queues = "comentario.cola.add")
    public void addComentario(@Payload Map<String, String> message) {
        comentarios comentario = new comentarios();
        comentario.setComentario(message.get("comentario"));

        // Define el formato que esperas en la cadena
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // Cambia esto según tu formato
        LocalDateTime fechaComentario;

        try {
            // Intenta parsear la fecha usando el formato definido
            fechaComentario = LocalDateTime.parse(message.get("fechaComentario"), formatter);
            comentario.setFechaComentario(fechaComentario);
        } catch (Exception e) {
            System.err.println("Error al parsear la fecha: " + e.getMessage());
            return; // O maneja el error de otra forma
        }

        // Obtener el video a partir del ID y asignarlo al comentario
        Long videoId = Long.valueOf(message.get("video"));
        videos video = videoService.findById(videoId);
        if (video != null) {
            comentario.setVideo(video);
        } else {
            System.err.println("Video no encontrado: " + videoId);
            return;
        }

        // Obtener el usuario por nombre de usuario
        String username = message.get("usuario");
        usuario user = clienteServicio.findByUsername(username);
        if (user != null) {
            comentario.setUsuario(user);
        } else {
            System.err.println("Usuario no encontrado: " + username);
            return;
        }

        // Guardar el comentario
        comentarioServicio.addComentario(comentario);
    }

    // Listener para eliminar un comentario
    @RabbitListener(queues = "comentario.cola.delete")
    public void deleteComentario(@Payload Map<String, String> message) {
        Long idComentario = Long.valueOf(message.get("idComentario"));
        String username = message.get("username"); // Si se necesita para verificar la propiedad
        comentarioServicio.deleteComentario(idComentario, username);
    }

    @RabbitListener(queues = "comentario.cola.edit")
    public void editComentario(@Payload Map<String, String> message) {
        Long idComentario = Long.valueOf(message.get("idComentario"));
        String nuevoComentario = message.get("nuevoComentario");
        String username = message.get("username"); // Si se necesita para verificar la propiedad

        comentarios actualizado = comentarioServicio.editComentario(idComentario, nuevoComentario, username);
        if (actualizado != null) {
            System.out.println("Comentario actualizado exitosamente: " + actualizado.getComentario());
        } else {
            System.err.println("Comentario no encontrado: " + idComentario);
        }
    }


}