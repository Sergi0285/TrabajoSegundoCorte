package Trabajo.Ingenieria.Rabbitmq;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {

     @Bean
    public TopicExchange exchange() {
        return new TopicExchange("notificacionesExchange");
    }

    @Bean
    public Binding bindingVideoCola(Queue colaSubirVideo, TopicExchange intercambioGeneral) {
        return BindingBuilder.bind(colaSubirVideo).to(intercambioGeneral).with("nuevoVideo.cola");
    }

    @Bean
    public Binding bindingSuscripcionCola(Queue colaSuscripcion, TopicExchange intercambioGeneral) {
        return BindingBuilder.bind(colaSuscripcion).to(intercambioGeneral).with("suscripcion.cola");
    }

    @Bean
    public Binding bindingMiniaturaCola(Queue colaMiniatura, TopicExchange intercambioGeneral) {
        return BindingBuilder.bind(colaMiniatura).to(intercambioGeneral).with("miniatura.cola");
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue colaMiniatura() {
        return new Queue("miniatura.cola", true);
    }

    @Bean
    public Queue colaSubirVideo() {
        return new Queue("video.cola", true);
    }

    @Bean
    public Queue colaAddComentario() {
        return new Queue("comentario.cola.add", true);
    }

    @Bean
    public Queue colaEditComentario() {
        return new Queue("comentario.cola.edit", true);
    }

    @Bean
    public Queue colaDeleteComentario() {
        return new Queue("comentario.cola.delete", true);
    }

    @Bean
    public Queue colaSuscripcion() {
        return new Queue("suscripcion.cola", true);
    }

    @Bean
    public Queue colaNuevoVideo() {
        return new Queue("nuevoVideo.cola", true);
    }
}