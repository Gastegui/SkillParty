package com.example.securingweb.ORM;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UsuarioService {

    private UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario guardarUsuario(Usuario usuario) 
    {
        System.out.println("NOMBRE: " + usuario.getNombre());
        System.out.println("CONTRASEÑA: " + usuario.getContraseña());
        System.out.println("ROL: " + usuario.getRol());
        try
        {
            return usuarioRepository.save(usuario);
        }
        catch (Exception e)
        {
            System.out.println("Ha saltado una excepción al guardar el usuario: " + e.getMessage());
            return null;
        }
    }

    // Otros métodos de servicio según sea necesario
}
