package Trabajo.Ingenieria.Servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Trabajo.Ingenieria.Entidades.suscripcion;
import Trabajo.Ingenieria.Entidades.usuario;
import Trabajo.Ingenieria.Repositorios.suscripcionRepositorio;
import Trabajo.Ingenieria.Repositorios.usuarioRepositorio;

import java.util.List;
import java.util.Optional;

@Service
public class suscripcionService {
    
    @Autowired
    private suscripcionRepositorio suscripcionRepo;

    @Autowired
    private usuarioRepositorio usuarioRepo;

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
        
        Optional<suscripcion> suscripcionOpt = suscripcionRepo.findByUsuarioIdAndVideoId(
            usuarioOpt.get().getId(), 
            canalId
        );
        
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
        return suscripcionRepo.existsByUsuarioIdAndVideoId(usuarioId, canalId);
    }

    /**
     * Obtiene todas las suscripciones de un canal específico
     */
    public List<suscripcion> getSuscripcionesByCanal(Long canalId) {
        return suscripcionRepo.findByVideoId(canalId);
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
        return suscripcionRepo.countByVideoId(canalId);
    }

    /**
     * Obtiene el número total de suscripciones de un usuario
     */
    public Long getNumeroSuscripciones(Long usuarioId) {
        return suscripcionRepo.countByUsuarioId(usuarioId);
    }
}