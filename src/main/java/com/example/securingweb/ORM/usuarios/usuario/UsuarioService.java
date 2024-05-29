package com.example.securingweb.ORM.usuarios.usuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.securingweb.ORM.usuarios.autoridad.Autoridad;
import com.example.securingweb.ORM.usuarios.autoridad.AutoridadRepository;



import java.util.ArrayList;
import java.util.List;

// Clase de servicio para gestionar operaciones relacionadas con los usuarios
@Service
public class UsuarioService implements UserDetailsService
{
    // Repositorios necesarios
    private UsuarioRepository usuarioRepository;
    private AutoridadRepository autoridadRepository;

    // Constructor con inyección de dependencias
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
        {
            throw new UsernameNotFoundException("No existe el usuario");
        }
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
                {
                    autoridades.add(autoridadExistente);
                }
                else
                {
                    autoridades.add(autoridadRepository.save(autoridad));
                }
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
            return null;
        }
    }

    @Transactional
    public void descontarSaldoCurso(Long usuarioId, Long cursoId) 
    {
        usuarioRepository.descontarSaldoCurso(usuarioId, cursoId);
    }

    @Transactional
    public void descontarSaldoServicio(Long usuarioId, Long servicioId, Long opcionId) 
    {
        usuarioRepository.descontarSaldoServicio(usuarioId, servicioId, opcionId);
    }
}