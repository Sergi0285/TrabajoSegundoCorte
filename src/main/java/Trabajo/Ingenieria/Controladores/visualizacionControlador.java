package Trabajo.Ingenieria.Controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import Trabajo.Ingenieria.Servicios.visualizacionServicio;

@RestController
@RequestMapping("/visualizaciones")
public class visualizacionControlador {

    @Autowired
    private visualizacionServicio visualizacionServ;

    // Cambia los @RequestParam a @RequestBod
    @PostMapping("/guardar")
    public ResponseEntity<?> guardarVisualizacion(@RequestBody VisualizacionRequest request) {
        try {
            visualizacionServ.guardarVisualizacion(request.getUsuarioid(), request.getVideoid());
            return ResponseEntity.ok("Visualización guardada correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al guardar la visualización: " + e.getMessage());
        }
    }
}

class VisualizacionRequest {
    private String usuarioid;  // Este debe ser un String
    private Long videoid;

    // Getters y setterss
    public String getUsuarioid() {
        return usuarioid;
    }

    public void setUsuarioid(String usuarioid) {
        this.usuarioid = usuarioid;
    }

    public Long getVideoid() {
        return videoid;
    }

    public void setVideoid(Long videoid) {
        this.videoid = videoid;
    }
}