package Trabajo.Ingenieria.Rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Map;
import Trabajo.Ingenieria.Servicios.videosServicio;

import java.io.IOException;


@Service
public class RabbitMQConsumers {

    @Autowired
    private videosServicio videoService;

    // Listener para almacenar el video
    @RabbitListener(queues = "video.cola")
    public void storeVideo(@Payload Map<String, String> message) throws IOException {
        String videoPath = (String) message.get("path");
        String file = (String) message.get("fileData");
        videoService.guardarVideo(videoPath, file);
    }
    
}