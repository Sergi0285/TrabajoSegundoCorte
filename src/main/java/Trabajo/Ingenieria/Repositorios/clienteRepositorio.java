package Trabajo.Ingenieria.Repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import Trabajo.Ingenieria.Entidades.role;
import Trabajo.Ingenieria.Entidades.usuario;

@Repository
public class clienteRepositorio {
    @Autowired
    private clienteCRUDrepositorio clienteCRUD;

    public usuario findByUsername(String username){
        return clienteCRUD.findByUsername(username);
    }    

    @SuppressWarnings("null")
    public usuario guardaraUsuario (usuario m){
        return clienteCRUD.save(m);
    }

    public role obtenerRolPorUsuario(String username) {
        usuario user = clienteCRUD.findByUsername(username);
        return user.getRol();
    }

    public List<usuario> getAllUsuarios() {
        return (List<usuario>) clienteCRUD.findAll();
    }

    public Optional<usuario> findById(Long id) {
        return clienteCRUD.findById(id);
    }
}
