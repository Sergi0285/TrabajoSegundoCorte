package Trabajo.Ingenieria.Servicios;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public suscripcion addSuscripcion(String usernameSubscriptor, Long canalId) {
        usuario suscriptor = usuarioRepo.findByUsername(usernameSubscriptor)
            .orElseThrow(() -> new RuntimeException("Suscriptor no encontrado"));
            
        usuario canal = usuarioRepo.findById(canalId)
            .orElseThrow(() -> new RuntimeException("Canal no encontrado"));

        if (suscripcionRepo.existsBySuscriptorIdAndCanalId(suscriptor.getId(), canalId)) {
            throw new RuntimeException("Ya existe una suscripción a este canal");
        }

        suscripcion nuevaSuscripcion = new suscripcion();
        nuevaSuscripcion.setFechaSuscripcion(LocalDateTime.now());
        nuevaSuscripcion.setSuscriptor(suscriptor);
        nuevaSuscripcion.setCanal(canal);

        return suscripcionRepo.save(nuevaSuscripcion);
    }

    @Transactional
    public boolean deleteSuscripcion(String username, Long canalId) {
        Optional<usuario> usuarioOpt = usuarioRepo.findByUsername(username);
        if (!usuarioOpt.isPresent()) {
            return false;
        }

        Optional<suscripcion> suscripcionOpt = suscripcionRepo.findBySuscriptorIdAndCanalId(
            usuarioOpt.get().getId(), 
            canalId
        );

        if (suscripcionOpt.isPresent()) {
            suscripcionRepo.delete(suscripcionOpt.get());
            return true;
        }
        return false;
    }

    public boolean existsSuscripcion(Long suscriptorId, Long canalId) {
        return suscripcionRepo.existsBySuscriptorIdAndCanalId(suscriptorId, canalId);
    }

    public Long getNumeroSuscriptores(Long canalId) {
        return suscripcionRepo.countByCanalId(canalId);
    }

    public List<suscripcion> getSuscripcionesByCanal(Long canalId) {
        return suscripcionRepo.findByCanalId(canalId);
    }
    public Map<String, Object> getDetallesCanal(Long canalId) {
        usuario canal = usuarioRepo.findById(canalId)
            .orElseThrow(() -> new RuntimeException("Canal no encontrado"));
            
        List<suscripcion> suscripciones = getSuscripcionesByCanal(canalId);
        List<Map<String, Object>> suscriptoresInfo = new ArrayList<>();
        
        for (suscripcion sub : suscripciones) {
            Map<String, Object> suscriptorInfo = new HashMap<>();
            suscriptorInfo.put("username", sub.getSuscriptor().getUsername());
            suscriptorInfo.put("fechaSuscripcion", sub.getFechaSuscripcion());
            suscriptoresInfo.add(suscriptorInfo);
        }
        
        Map<String, Object> detalles = new HashMap<>();
        detalles.put("nombreCanal", canal.getUsername());
        detalles.put("totalSuscriptores", suscripciones.size());
        detalles.put("suscriptores", suscriptoresInfo);
        
        return detalles;
    }
}