package Trabajo.Ingenieria.Servicios;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import Trabajo.Ingenieria.Entidades.role;
import Trabajo.Ingenieria.Entidades.usuario;
import Trabajo.Ingenieria.Repositorios.clienteRepositorio;
import Trabajo.Ingenieria.Repositorios.usuarioRepositorio;
import io.jsonwebtoken.io.IOException;

@Service
public class clienteServicio {
    @Autowired
    private clienteRepositorio usuarioRepository;
    
    @Autowired
    private usuarioRepositorio userRepositorio;
    
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

    public ResponseEntity<Resource> streamProfileImage(String username) throws IOException {
        // Busca al usuario en la base de datos
        usuario usuario = userRepositorio.findByUsername(username)
                          .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    
        // Obtiene la ruta de la imagen desde el atributo 'perfil' del usuario
        Path profileImagePath = Paths.get(usuario.getPerfil());
    
        Resource profileImageResource;
        try {
            // Crea un recurso desde la ruta de la imagen
            profileImageResource = new UrlResource(profileImagePath.toUri());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error al convertir la ruta a URL: " + profileImagePath.toString(), e);
        }
    
        // Verifica si el recurso es legible
        if (!profileImageResource.exists() || !profileImageResource.isReadable()) {
            throw new RuntimeException("No se puede leer la imagen de perfil: " + profileImagePath.toString());
        }
    
        // Retorna la imagen como respuesta HTTP
        return ResponseEntity.ok()
                .contentType(MediaTypeFactory.getMediaType(profileImageResource).orElse(MediaType.APPLICATION_OCTET_STREAM))
                .body(profileImageResource);
    }
    
        public usuario update(usuario usuarioActualizado) {
        // Busca el usuario existente por su username
        Optional<usuario> usuarioExistenteOpt = userRepositorio.findByUsername(usuarioActualizado.getUsername());

        if (usuarioExistenteOpt.isPresent()) {
            usuario usuarioExistente = usuarioExistenteOpt.get();
            // Actualiza los campos necesarios
            usuarioExistente.setNombre(usuarioActualizado.getNombre());
            usuarioExistente.setCorreo(usuarioActualizado.getCorreo());
            usuarioExistente.setCelular(usuarioActualizado.getCelular());
            // Puedes agregar lógica para manejar la imagen de perfil si es necesario

            return usuarioRepository.guardaraUsuario(usuarioExistente); // Guardar el usuario actualizado
        } else {
            throw new RuntimeException("Usuario no encontrado");
        }
    }

    public usuario findById(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }
}
