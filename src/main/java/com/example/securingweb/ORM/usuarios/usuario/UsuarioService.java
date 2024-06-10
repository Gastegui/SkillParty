package com.example.securingweb.ORM.usuarios.usuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.securingweb.ORM.cursos.curso.Curso;
import com.example.securingweb.ORM.cursos.curso.CursoRepository;
import com.example.securingweb.ORM.cursos.tipos.Tipo;
import com.example.securingweb.ORM.cursos.tipos.TipoRepository;
import com.example.securingweb.ORM.usuarios.autoridad.Autoridad;
import com.example.securingweb.ORM.usuarios.autoridad.AutoridadRepository;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

// Clase de servicio para gestionar operaciones relacionadas con los usuarios
@Service
public class UsuarioService implements UserDetailsService
{
    private UsuarioRepository usuarioRepository;
    private AutoridadRepository autoridadRepository;
    private CursoRepository cursoRepository;
    private TipoRepository tipoRepository;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, AutoridadRepository autoridadRepository,
                            CursoRepository cursoRepository, TipoRepository tipoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.autoridadRepository = autoridadRepository;
        this.cursoRepository = cursoRepository;
        this.tipoRepository = tipoRepository;
    }

    public List<Usuario> obtenerTodosLosUsuarios() 
    {
        return usuarioRepository.findAll();
    }

    @Override
    public Usuario loadUserByUsername(String nombre) 
    {   
        Optional<Usuario> ret = usuarioRepository.findByUsername(nombre);
        if (ret.isEmpty()) 
        {
            throw new UsernameNotFoundException("No existe el usuario");
        }
        return ret.get();
    }

    public Optional<Usuario> findByUsername(String username) 
    {
        return usuarioRepository.findByUsername(username);
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
        catch (UsernameNotFoundException e) 
        {
            return null;
        } 
        catch (Exception e) 
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

    public int calcularEdad(Date fechaNacimiento) {
        // Convertir java.sql.Date a java.util.Date si es necesario
        if (fechaNacimiento instanceof java.sql.Date) {
            fechaNacimiento = new Date(fechaNacimiento.getTime());
        }
        LocalDate birthDate = fechaNacimiento.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    public List<Curso> obtenerRecomendacionesCursos(int edad) 
    {
        List<Curso> cursosRecomendados = new ArrayList<>();
        Tipo arte = tipoRepository.findByDescripcion("Arte");
        Tipo ciencia = tipoRepository.findByDescripcion("Ciencia");
        Tipo informatica = tipoRepository.findByDescripcion("Informática");
        Tipo geografia = tipoRepository.findByDescripcion("Geografía");

        if (edad >= 0 && edad < 20) {
            cursosRecomendados.addAll(cursoRepository.findByTipoAndPuntuacionGreaterThan(arte, 6L));
            cursosRecomendados.addAll(cursoRepository.findByTipoAndPuntuacionGreaterThan(ciencia, 6L));
            cursosRecomendados.addAll(cursoRepository.findByTipoAndPuntuacionLessThan(informatica, 9L));
            cursosRecomendados.addAll(cursoRepository.findByTipoAndPuntuacionGreaterThan(geografia, 4L));
        } else if (edad >= 20 && edad < 40) {
            cursosRecomendados.addAll(cursoRepository.findByTipoAndPuntuacion(arte, 7L));
            cursosRecomendados.addAll(cursoRepository.findByTipoAndPuntuacion(ciencia, 7L));
            cursosRecomendados.addAll(cursoRepository.findByTipoAndPuntuacion(informatica, 7L));
            cursosRecomendados.addAll(cursoRepository.findByTipoAndPuntuacion(geografia, 5L));
        } else if (edad >= 40 && edad < 60) {
            cursosRecomendados.addAll(cursoRepository.findByTipoAndPuntuacion(arte, 8L));
            cursosRecomendados.addAll(cursoRepository.findByTipoAndPuntuacion(ciencia, 8L));
            cursosRecomendados.addAll(cursoRepository.findByTipoAndPuntuacion(informatica, 4L));
            cursosRecomendados.addAll(cursoRepository.findByTipoAndPuntuacion(geografia, 7L));
        } else if (edad >= 60 && edad < 80) {
            cursosRecomendados.addAll(cursoRepository.findByTipoAndPuntuacionLessThan(arte, 9L));
            cursosRecomendados.addAll(cursoRepository.findByTipoAndPuntuacionLessThan(ciencia, 9L));
            cursosRecomendados.addAll(cursoRepository.findByTipoAndPuntuacionGreaterThan(informatica, 1L));
            cursosRecomendados.addAll(cursoRepository.findByTipoAndPuntuacionLessThan(geografia, 8L));
        }
        return cursosRecomendados;
    }

    public Recomendaciones obtenerRecomendaciones(Long usuarioId) 
    {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        int edad = calcularEdad(usuario.getFechaDeNacimiento());

        List<Curso> cursosRecomendados = obtenerRecomendacionesCursos(edad);

        return new Recomendaciones(cursosRecomendados);
    }
}