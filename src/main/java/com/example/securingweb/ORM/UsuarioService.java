package com.example.securingweb.ORM;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class UsuarioService implements UserDetailsService{

    private UsuarioRepository usuarioRepository;
    private AutoridadRepository autoridadRepository;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, AutoridadRepository autoridadRepository) 
    {
        this.usuarioRepository = usuarioRepository;
        this.autoridadRepository = autoridadRepository;
    }

    public List<Usuario> obtenerTodosLosUsuarios() 
    {
        return usuarioRepository.findAll();
    }

    @Override
    public Usuario loadUserByUsername(String nombre)
    {   
        Usuario ret = usuarioRepository.findByUsername(nombre);
        if(ret == null)
            throw new UsernameNotFoundException("No existe el usuario");
        
        return usuarioRepository.findByUsername(nombre);
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

            usuario.setAutoridadesEntity(autoridades);

            return usuarioRepository.save(usuario);
        }
        catch(UsernameNotFoundException e)
        {
            return null;
        }
        catch(Exception e)
        {
            System.out.println("Ha saltado una excepción al guardar el usuario: " + e.getMessage());
            return null;
        }
    }

}
