package Trabajo.Ingenieria.Servicios;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import Trabajo.Ingenieria.DTOs.NotificacionSuscripcion;
import Trabajo.Ingenieria.DTOs.NotificacionNuevoVideo;

@Service
public class NotificationService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void enviarNotificacionSuscripcion(Long idCanal, String emailSuscriptor) {
        NotificacionSuscripcion notificacion = new NotificacionSuscripcion(idCanal, emailSuscriptor);
        rabbitTemplate.convertAndSend("suscripcion.cola", notificacion);
    }

    public void enviarNotificacionNuevoVideo(Long idCanal, String tituloVideo, String emailSuscriptores) {
        NotificacionNuevoVideo notificacion = new NotificacionNuevoVideo(idCanal, tituloVideo, emailSuscriptores);
        rabbitTemplate.convertAndSend("nuevoVideo.cola", notificacion);
    }
    
}
