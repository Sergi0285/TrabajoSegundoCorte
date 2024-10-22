package Trabajo.Ingenieria.Servicios;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Trabajo.Ingenieria.DTOs.NotificacionNuevoVideo;
import Trabajo.Ingenieria.Entidades.suscripcion;
import Trabajo.Ingenieria.Entidades.usuario;
import Trabajo.Ingenieria.Repositorios.suscripcionRepositorio;
import Trabajo.Ingenieria.Repositorios.usuarioRepositorio;

@Service
public class suscripcionService {

    @Autowired
    private suscripcionRepositorio suscripcionRepo;

    @Autowired
    private usuarioRepositorio usuarioRepo;

    @Autowired
    private JavaMailSender javaMailSender;

    /**
     * Agrega una nueva suscripción
     */
    @Transactional
    public suscripcion addSuscripcion(suscripcion nuevaSuscripcion) {
        return suscripcionRepo.save(nuevaSuscripcion);
    }

    /**
     * Elimina una suscripción dado el username del suscriptor y el ID del canal
     */
    @Transactional
    public boolean deleteSuscripcion(String username, Long canalId) {
        Optional<usuario> usuarioOpt = usuarioRepo.findByUsername(username);
        if (!usuarioOpt.isPresent()) {
            return false;
        }

        Optional<suscripcion> suscripcionOpt = suscripcionRepo.findByUsuarioIdAndVideoIdVideo(
                usuarioOpt.get().getId(),
                canalId);

        if (suscripcionOpt.isPresent()) {
            suscripcionRepo.delete(suscripcionOpt.get());
            return true;
        }
        return false;
    }

    /**
     * Verifica si existe una suscripción entre un usuario y un canal
     */
    public boolean existsSuscripcion(Long usuarioId, Long canalId) {
        return suscripcionRepo.existsByUsuarioIdAndVideoIdVideo(usuarioId, canalId);
    }

    /**
     * Obtiene todas las suscripciones de un canal específico
     */
    public List<suscripcion> getSuscripcionesByCanal(Long canalId) {
        return suscripcionRepo.findByVideoIdVideo(canalId);
    }

    /**
     * Obtiene todas las suscripciones de un usuario específico
     */
    public List<suscripcion> getSuscripcionesByUsuario(Long usuarioId) {
        return suscripcionRepo.findByUsuarioId(usuarioId);
    }

    /**
     * Obtiene el número total de suscriptores de un canal
     */
    public Long getNumeroSuscriptores(Long canalId) {
        return suscripcionRepo.countByVideoIdVideo(canalId);
    }

    /**
     * Obtiene el número total de suscripciones de un usuario
     */
    public Long getNumeroSuscripciones(Long usuarioId) {
        return suscripcionRepo.countByUsuarioId(usuarioId);
    }

    public void enviarNotificacionesNuevoVideo(NotificacionNuevoVideo notificacion) {
        // Lógica para enviar notificaciones a los suscriptores
        List<String> correosSuscriptores = suscripcionRepo.findByVideoIdVideo(notificacion.getIdCanal())
            .stream()
            .map(suscripcion -> suscripcion.getUsuario().getEmail())
            .collect(Collectors.toList());
    
        for (String email : correosSuscriptores) {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setTo(email);
            mensaje.setSubject("Nuevo Video: " + notificacion.getTituloVideo());
            mensaje.setText("Se ha subido un nuevo video en el canal al que estás suscrito.");
    
            try {
                javaMailSender.send(mensaje);
                System.out.println("Notificación enviada a: " + email);
            } catch (Exception e) {
                System.err.println("Error al enviar la notificación a " + email + ": " + e.getMessage());
            }
        }
    }
    
}