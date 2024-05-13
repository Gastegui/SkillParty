package com.example.securingweb.ORM;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class UsuarioService implements UserDetailsService{

    private UsuarioRepository usuarioRepository;
    private AutoridadRepository autoridadRepository;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, AutoridadRepository autoridadRepository) {
        this.usuarioRepository = usuarioRepository;
        this.autoridadRepository = autoridadRepository;
    }

    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }
    @Override
    public Usuario loadUserByUsername(String nombre)
    {
        Usuario ret = usuarioRepository.findByUsername(nombre);
        //MARK: AQUI ANTES LE PONIA LOS ROLES 
        return ret;
    }

    public Usuario guardarUsuario(Usuario usuario) 
    {
        try
        {
            List<Autoridad> autoridades = new ArrayList<>();
            for (Autoridad autoridad : usuario.getAuthoritiesEntity()) 
            {
                Autoridad autoridadExistente = autoridadRepository.findByAutoridad(autoridad.getAutoridad());
                if (autoridadExistente != null) 
                    autoridades.add(autoridadExistente);
                else
                    autoridades.add(autoridadRepository.save(autoridad));
            }

            // Paso 2: Asigna las autoridades al usuario
            usuario.setAutoridadesEntity(autoridades);

            // Paso 3: Guarda el usuario en la base de datos
            return usuarioRepository.save(usuario);
        }
        catch (Exception e)
        {
            System.out.println("Ha saltado una excepción al guardar el usuario: " + e.getMessage());
            return null;
        }
    }

}
