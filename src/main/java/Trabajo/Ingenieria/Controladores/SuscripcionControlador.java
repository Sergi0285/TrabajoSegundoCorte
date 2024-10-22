package Trabajo.Ingenieria.Controladores;

import java.util.List;
import java.util.Map;

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

import Trabajo.Ingenieria.Entidades.suscripcion;
import Trabajo.Ingenieria.Entidades.usuario;
import Trabajo.Ingenieria.Servicios.clienteServicio;
import Trabajo.Ingenieria.Servicios.suscripcionService;

@RestController
@RequestMapping("/suscripciones")
public class SuscripcionControlador {
    @Autowired
    private clienteServicio clienteServicio;

    @Autowired
    private suscripcionService suscripcionServicio;

    @PostMapping("/suscribirse")
    public ResponseEntity<?> crearSuscripcion(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            Long canalId = Long.valueOf(request.get("canalId"));
            
            suscripcion nuevaSuscripcion = suscripcionServicio.addSuscripcion(username, canalId);
            return new ResponseEntity<>("Suscripción realizada exitosamente", HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al procesar la suscripción", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/cancelar")
    public ResponseEntity<String> cancelarSuscripcion(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            Long canalId = Long.valueOf(request.get("canalId"));

            boolean cancelada = suscripcionServicio.deleteSuscripcion(username, canalId);
            if (cancelada) {
                return ResponseEntity.ok("Suscripción cancelada exitosamente");
            } else {
                return new ResponseEntity<>("Suscripción no encontrada", HttpStatus.NOT_FOUND);
            }
        } catch (NumberFormatException e) {
            return new ResponseEntity<>("Error al cancelar la suscripción", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/canal/{canalId}")
    public ResponseEntity<List<suscripcion>> obtenerSuscriptoresPorCanal(@PathVariable Long canalId) {
        try {
            List<suscripcion> suscripciones = suscripcionServicio.getSuscripcionesByCanal(canalId);
            return ResponseEntity.ok(suscripciones);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/canal/{canalId}/detalles")
    public ResponseEntity<Map<String, Object>> obtenerDetallesCanal(@PathVariable Long canalId) {
        try {
            Map<String, Object> detalles = suscripcionServicio.getDetallesCanal(canalId);
            return ResponseEntity.ok(detalles);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/usuario/{username}")
    public ResponseEntity<List<suscripcion>> obtenerSuscripcionesDeUsuario(@PathVariable String username) {
        try {
            usuario user = clienteServicio.findByUsername(username);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            List<suscripcion> suscripciones = suscripcionServicio.getSuscripcionesByCanal(user.getId());
            return ResponseEntity.ok(suscripciones);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/verificar")
    public ResponseEntity<Boolean> verificarSuscripcion(
            @RequestParam String username, 
            @RequestParam Long canalId) {
        try {
            usuario user = clienteServicio.findByUsername(username);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            boolean estaSuscrito = suscripcionServicio.existsSuscripcion(user.getId(), canalId);
            return ResponseEntity.ok(estaSuscrito);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/contador/{canalId}")
    public ResponseEntity<Long> obtenerNumeroSuscriptores(@PathVariable Long canalId) {
        try {
            Long numeroSuscriptores = suscripcionServicio.getNumeroSuscriptores(canalId);
            return ResponseEntity.ok(numeroSuscriptores);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/estado-general/{canalId}")
    public ResponseEntity<Map<String, Object>> obtenerEstadoGeneral(@PathVariable Long canalId) {
        try {
            Map<String, Object> detalles = suscripcionServicio.getDetallesCanal(canalId);
            return ResponseEntity.ok(detalles);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}