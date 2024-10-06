package Trabajo.Ingenieria.Servicios;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import Trabajo.Ingenieria.Entidades.role;
import Trabajo.Ingenieria.Entidades.usuario;
import Trabajo.Ingenieria.Repositorios.clienteRepositorio;

@Service
public class clienteServicio {
  @Autowired
    private clienteRepositorio usuarioRepository;
    
    public usuario findByUsername(String username){
        return usuarioRepository.findByUsername(username);
    }

    public role obtenerRolPorUsuario(String username) {
        usuario user = usuarioRepository.findByUsername(username);
        return user.getRol();
    }

    public List<usuario> getAllUsuarios(){
        return usuarioRepository.getAllUsuarios();
    }    

    public usuario save(usuario k){
        return usuarioRepository.guardaraUsuario(k);
    }
}
