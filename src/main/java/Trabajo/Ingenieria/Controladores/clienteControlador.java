package Trabajo.Ingenieria.Controladores;

import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import Trabajo.Ingenieria.Entidades.usuario;
import Trabajo.Ingenieria.Servicios.clienteServicio;

@RestController
@RequestMapping("/controladorCliente")
public class clienteControlador {
     @Autowired
private clienteServicio usuarioService;
    @GetMapping("/usuario")
    public List<usuario> getAllUs(){
        return usuarioService.getAllUsuarios();
    }

    @PostMapping("/guardarUs")
    public usuario guardarUsuario(@RequestBody usuario k){
        return usuarioService.save(k);
    }
    @GetMapping("/findbyalias")
    public usuario findByUsername(@RequestParam("username") String username){
        return usuarioService.findByUsername(username);
    }
    @GetMapping("/rol")
    public String obtenerRolPorUsuario() {
        // Obtener el nombre de usuario del objeto Authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        // Buscar el usuario en el servicio Usuario_modelos
        usuario user = usuarioService.findByUsername(username);

        // Verificar si el usuario existe y devolver su rol
        if (user != null) {
            return user.getRol().toString();
        } else {
            return "Usuario no encontrado";
        }
    }
}
