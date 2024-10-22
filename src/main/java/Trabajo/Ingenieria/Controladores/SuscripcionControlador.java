package Trabajo.Ingenieria.Controladores;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import Trabajo.Ingenieria.DTOs.NotificacionSuscripcion;
import Trabajo.Ingenieria.Entidades.suscripcion;
import Trabajo.Ingenieria.Entidades.usuario;
import Trabajo.Ingenieria.Entidades.videos;
import Trabajo.Ingenieria.Servicios.NotificationService;
import Trabajo.Ingenieria.Servicios.clienteServicio;
import Trabajo.Ingenieria.Servicios.suscripcionService;
import Trabajo.Ingenieria.Servicios.videosServicio;

@RestController
@RequestMapping("/suscripciones")
public class SuscripcionControlador {
    @Autowired
    private clienteServicio clienteServicio;

    @Autowired
    private suscripcionService suscripcionServicio;

    @Autowired
    private videosServicio videoServicio;

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/suscribirse")
    public ResponseEntity<String> crearSuscripcion(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        Long canalId = Long.parseLong(request.get("canalId"));

        // Buscar el canal por su ID
        videos canal = videoServicio.findById(canalId);
        if (canal == null) {
            return new ResponseEntity<>("Canal no encontrado", HttpStatus.NOT_FOUND);
        }

        // Buscar el usuario suscriptor por su nombre de usuario
        usuario suscriptor = clienteServicio.findByUsername(username);
        if (suscriptor == null) {
            return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
        }

        // Verificar si ya existe la suscripción
        if (suscripcionServicio.existsSuscripcion(suscriptor.getId(), canalId)) {
            return new ResponseEntity<>("Ya existe una suscripción a este canal", HttpStatus.BAD_REQUEST);
        }

        // Crear la suscripción
        suscripcion nuevaSuscripcion = new suscripcion();
        nuevaSuscripcion.setFechaSuscripcion(LocalDateTime.now());
        nuevaSuscripcion.setUsuario(suscriptor);
        nuevaSuscripcion.setVideo(canal);

        // Guardar la suscripción
        suscripcionServicio.addSuscripcion(nuevaSuscripcion);

        return new ResponseEntity<>("Suscripción realizada exitosamente", HttpStatus.CREATED);
    }

    @DeleteMapping("/cancelar")
    public ResponseEntity<String> cancelarSuscripcion(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        Long canalId = Long.parseLong(request.get("canalId"));

        boolean cancelada = suscripcionServicio.deleteSuscripcion(username, canalId);
        if (cancelada) {
            return ResponseEntity.ok("Suscripción cancelada exitosamente");
        } else {
            return new ResponseEntity<>("Suscripción no encontrada", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/canal/{canalId}")
    public ResponseEntity<List<suscripcion>> obtenerSuscriptoresPorCanal(@PathVariable Long canalId) {
        List<suscripcion> suscripciones = suscripcionServicio.getSuscripcionesByCanal(canalId);
        return ResponseEntity.ok(suscripciones);
    }

    @GetMapping("/usuario/{username}")
    public ResponseEntity<List<suscripcion>> obtenerSuscripcionesDeUsuario(@PathVariable String username) {
        usuario user = clienteServicio.findByUsername(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        List<suscripcion> suscripciones = suscripcionServicio.getSuscripcionesByUsuario(user.getId());
        return ResponseEntity.ok(suscripciones);
    }

    @GetMapping("/verificar")
    public ResponseEntity<Boolean> verificarSuscripcion(@RequestParam String username, @RequestParam Long canalId) {
        usuario user = clienteServicio.findByUsername(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        boolean estaSuscrito = suscripcionServicio.existsSuscripcion(user.getId(), canalId);
        return ResponseEntity.ok(estaSuscrito);
    }
    @GetMapping("/contador/{canalId}")
    public ResponseEntity<Long> obtenerNumeroSuscriptores(@PathVariable Long canalId) {
        // Verificar si el canal existe
        videos canal = videoServicio.findById(canalId);
        if (canal == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Obtener el número de suscriptores
        Long numeroSuscriptores = suscripcionServicio.getNumeroSuscriptores(canalId);
        return ResponseEntity.ok(numeroSuscriptores);
    }

    @GetMapping("/total-suscripciones/{username}")
    public ResponseEntity<Long> obtenerTotalSuscripciones(@PathVariable String username) {
        // Buscar el usuario
        usuario user = clienteServicio.findByUsername(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Obtener el número total de suscripciones del usuario
        Long totalSuscripciones = suscripcionServicio.getNumeroSuscripciones(user.getId());
        return ResponseEntity.ok(totalSuscripciones);
    }
    
    @GetMapping("/estado-general/{canalId}")
    public ResponseEntity<Map<String, Object>> obtenerEstadoGeneral(@PathVariable Long canalId) {
        try {
            videos canal = videoServicio.findById(canalId);
            if (canal == null) {
                return ResponseEntity.notFound().build();
            }

            Map<String, Object> estado = new HashMap<>();
            estado.put("numeroSuscriptores", suscripcionServicio.getNumeroSuscriptores(canalId));
            estado.put("fechaCreacionCanal", canal.getFechaSubida());
            // Puedes agregar más información relevante aquí

            return ResponseEntity.ok(estado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/canal/{canalId}/detalles")
public ResponseEntity<Map<String, Object>> obtenerDetallesCanal(@PathVariable Long canalId) {
    videos canal = videoServicio.findById(canalId);
    if (canal == null) {
        return ResponseEntity.notFound().build();
    }

    List<suscripcion> suscripciones = suscripcionServicio.getSuscripcionesByCanal(canalId);
    List<Map<String, Object>> suscriptoresInfo = suscripciones.stream()
        .map(sub -> {
            Map<String, Object> info = new HashMap<>();
            info.put("username", sub.getUsuario().getUsername());
            info.put("fechaSuscripcion", sub.getFechaSuscripcion());
            return info;
        })
        .collect(Collectors.toList());

    Map<String, Object> detalles = new HashMap<>();
    detalles.put("nombreCanal", canal.getUsuario().getUsername());
    detalles.put("suscriptores", suscriptoresInfo);
    detalles.put("totalSuscriptores", suscripciones.size());

    return ResponseEntity.ok(detalles);
}
 // Nuevo endpoint para manejar la notificación de suscripción
    @PostMapping("/notificar")
    public ResponseEntity<?> notificarSuscripcion(@RequestBody NotificacionSuscripcion notificacion) {
        notificationService.enviarNotificacionSuscripcion(notificacion.getIdCanal(), notificacion.getEmailSuscriptor());
        return ResponseEntity.ok("Notificación de suscripción enviada");
    }
}