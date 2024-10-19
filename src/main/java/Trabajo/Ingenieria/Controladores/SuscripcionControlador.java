package Trabajo.Ingenieria.Controladores;

import java.util.Map;
import java.util.List;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import Trabajo.Ingenieria.Entidades.suscripcion;
import Trabajo.Ingenieria.Entidades.usuario;
import Trabajo.Ingenieria.Entidades.videos;
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
}