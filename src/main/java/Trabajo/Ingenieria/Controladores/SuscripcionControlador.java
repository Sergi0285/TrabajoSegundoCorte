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

import Trabajo.Ingenieria.DTOs.NotificacionNuevoVideo;
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

    // Verificar que el video exista
    videos canal = videoServicio.findById(canalId);
    if (canal == null) {
        return new ResponseEntity<>("Canal no encontrado", HttpStatus.NOT_FOUND);
    }

    usuario dueñoCanal = canal.getUsuario();
    if (dueñoCanal == null) {
        return new ResponseEntity<>("El canal no tiene un dueño asignado", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    usuario suscriptor = clienteServicio.findByUsername(username);
    if (suscriptor == null) {
        return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
    }

    // Suscribir al usuario a todos los videos del dueño
    List<videos> videosDelDueño = videoServicio.findByUsuarioId(dueñoCanal.getId());
    videosDelDueño.forEach(video -> {
        if (!suscripcionServicio.existsSuscripcion(suscriptor.getId(), video.getIdVideo())) {
            suscripcion nuevaSuscripcion = new suscripcion();
            nuevaSuscripcion.setUsuario(suscriptor);
            nuevaSuscripcion.setVideo(video);
            nuevaSuscripcion.setCanalUsuario(dueñoCanal);
            nuevaSuscripcion.setFechaSuscripcion(LocalDateTime.now());
            suscripcionServicio.addSuscripcion(nuevaSuscripcion, video.getIdVideo());
        }
    });

    return new ResponseEntity<>("Suscripción realizada exitosamente", HttpStatus.CREATED);
}


@DeleteMapping("/cancelar")
public ResponseEntity<String> cancelarSuscripcion(@RequestBody Map<String, String> request) {
    String username = request.get("username");
    Long canalId = Long.parseLong(request.get("canalId"));

    videos canal = videoServicio.findById(canalId);
    if (canal == null) {
        return new ResponseEntity<>("Canal no encontrado", HttpStatus.NOT_FOUND);
    }

    usuario dueñoCanal = canal.getUsuario();
    if (dueñoCanal == null) {
        return new ResponseEntity<>("El canal no tiene un dueño asignado", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    usuario suscriptor = clienteServicio.findByUsername(username);
    if (suscriptor == null) {
        return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
    }

    List<videos> videosDelDueño = videoServicio.findByUsuarioId(dueñoCanal.getId());
    videosDelDueño.forEach(video -> {
        suscripcionServicio.deleteSuscripcion(suscriptor.getUsername(), video.getIdVideo());
    });

    return ResponseEntity.ok("Suscripción cancelada exitosamente");
}

    @GetMapping("/total-suscriptores/{usuarioId}")
    public ResponseEntity<Long> obtenerTotalSuscriptores(@PathVariable Long usuarioId) {
        Long totalSuscriptores = suscripcionServicio.getTotalSuscriptoresPorUsuario(usuarioId);
        return ResponseEntity.ok(totalSuscriptores);
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

    @GetMapping("/usuario-total-suscriptores/{usuarioId}")
    public ResponseEntity<Map<String, Object>> obtenerTotalSuscriptoresUsuario(@PathVariable Long usuarioId) {
        Long totalSuscriptores = suscripcionServicio.getTotalSuscriptoresPorUsuario(usuarioId);
        usuario usuario = clienteServicio.findById(usuarioId);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

    Map<String, Object> respuesta = new HashMap<>();
    respuesta.put("totalSuscriptores", totalSuscriptores);
    respuesta.put("nombreUsuario", usuario.getUsername());
    return ResponseEntity.ok(respuesta);
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

    // Obtener el nombre del propietario del canal (usuario que subió el video)
    String nombrePropietario = canal.getUsuario().getNombre(); // Supongamos que el campo es "nombre"
    
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
    detalles.put("nombreCanal", canal.getUsuario().getUsername()); // Este ya lo tienes
    detalles.put("nombrePropietario", nombrePropietario); // Nuevo campo con el nombre real del usuario
    detalles.put("suscriptores", suscriptoresInfo);
    detalles.put("totalSuscriptores", suscripciones.size());

    return ResponseEntity.ok(detalles);
}

@GetMapping("/video-detalles/{id}")
public ResponseEntity<Map<String, Object>> obtenerDetallesVideo(@PathVariable Long id) {
    videos video = videoServicio.findById(id);
    if (video == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    Map<String, Object> detalles = new HashMap<>();
    detalles.put("titulo", video.getTitulo());
    detalles.put("descripcion", video.getDescripcion());
    detalles.put("usuarioAlias", video.getUsuario().getUsername()); // Alias (username) del propietario
    detalles.put("usuarioNombre", video.getUsuario().getNombre()); // Nombre del propietario

    return ResponseEntity.ok(detalles);
}

    @GetMapping("/propietario-seguidores/{alias}")
    public ResponseEntity<List<Map<String, Object>>> obtenerSeguidoresPropietario(@PathVariable String alias) {
    usuario propietario = clienteServicio.findByUsername(alias);
        if (propietario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        List<suscripcion> suscripciones = suscripcionServicio.getSuscripcionesByUsuario(propietario.getId());
        if (suscripciones == null || suscripciones.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
     }

        List<Map<String, Object>> seguidores = suscripciones.stream()
            .map(suscripcion -> {
             Map<String, Object> seguidorInfo = new HashMap<>();
                seguidorInfo.put("alias", suscripcion.getUsuario().getUsername());
                seguidorInfo.put("fecha", suscripcion.getFechaSuscripcion());
                return seguidorInfo;
            })
            .collect(Collectors.toList());

        return ResponseEntity.ok(seguidores);
    }
 // Nuevo endpoint para manejar la notificación de suscripción
    @PostMapping("/notificar")
    public ResponseEntity<?> notificarSuscripcion(@RequestBody NotificacionSuscripcion notificacion) {
        notificationService.enviarNotificacionSuscripcion(notificacion.getIdCanal(), notificacion.getEmailSuscriptor());
        return ResponseEntity.ok("Notificación de suscripción enviada");
    }

    @PostMapping("/notificarNuevoVideo")
    public ResponseEntity<String> notificarNuevoVideo(@RequestBody NotificacionNuevoVideo notificacion) {
        try {
            // Llamada no estática al método del servicio
            suscripcionServicio.enviarNotificacionesNuevoVideo(notificacion);
            return ResponseEntity.ok("Notificación enviada a los suscriptores");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al enviar notificación: " + e.getMessage());
        }
    }
}