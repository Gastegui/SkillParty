package com.example.securingweb.ORM.usuarios.usuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.securingweb.ORM.servicios.categoria.Categoria;
import com.example.securingweb.ORM.servicios.categoria.CategoriaRepository;
import com.example.securingweb.ORM.servicios.servicio.Servicio;
import com.example.securingweb.ORM.servicios.servicio.ServicioRepository;
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
    //private CursoRepository cursoRepository;
    private ServicioRepository servicioRepository;
    private CategoriaRepository categoriaRepository;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, AutoridadRepository autoridadRepository,
                          /*CursoRepository cursoRepository, */ServicioRepository servicioRepository,
                            CategoriaRepository categoriaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.autoridadRepository = autoridadRepository;
        //this.cursoRepository = cursoRepository;
        this.servicioRepository = servicioRepository;
        this.categoriaRepository = categoriaRepository;
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

    /*public List<Curso> obtenerRecomendacionesCursos(int edad) 
    {
        List<Curso> cursosRecomendados = new ArrayList<>();
        if (edad >= 0 && edad < 20) 
        {
            cursosRecomendados = cursoRepository.findByCategoriaAndPuntuacionGreaterThan("ARTE", 6);
        } 
        else if (edad >= 20 && edad < 40) 
        {
            cursosRecomendados = cursoRepository.findByCategoriaAndPuntuacion("CIENCIA", 8);
        } 
        else if (edad >= 40 && edad < 60) 
        {
            cursosRecomendados = cursoRepository.findByCategoriaAndPuntuacion("INFORMATICA", 4);
        } 
        else if (edad >= 60 && edad < 80) 
        {
            cursosRecomendados = cursoRepository.findByCategoriaAndPuntuacionLessThan("GEOGRAFIA", 7);
        }
        return cursosRecomendados;
    }*/

    public List<Servicio> obtenerRecomendacionesServicios(int edad) 
    {
        List<Servicio> serviciosRecomendados = new ArrayList<>();
        Categoria arte = categoriaRepository.findByDescripcion("Arte");
        Categoria ciencia = categoriaRepository.findByDescripcion("Ciencia");
        Categoria informatica = categoriaRepository.findByDescripcion("Informática");
        Categoria geografia = categoriaRepository.findByDescripcion("Geografía");

        if (edad >= 0 && edad < 20) {
            serviciosRecomendados.addAll(servicioRepository.findByCategoriaAndPuntuacionGreaterThan(arte, 6L));
            serviciosRecomendados.addAll(servicioRepository.findByCategoriaAndPuntuacionGreaterThan(ciencia, 7L));
            serviciosRecomendados.addAll(servicioRepository.findByCategoriaAndPuntuacionLessThan(informatica, 6L));
            serviciosRecomendados.addAll(servicioRepository.findByCategoriaAndPuntuacionGreaterThan(geografia, 4L));
        } else if (edad >= 20 && edad < 40) {
            serviciosRecomendados.addAll(servicioRepository.findByCategoriaAndPuntuacion(arte, 7L));
            serviciosRecomendados.addAll(servicioRepository.findByCategoriaAndPuntuacion(ciencia, 8L));
            serviciosRecomendados.addAll(servicioRepository.findByCategoriaAndPuntuacion(informatica, 5L));
            serviciosRecomendados.addAll(servicioRepository.findByCategoriaAndPuntuacion(geografia, 5L));
        } else if (edad >= 40 && edad < 60) {
            serviciosRecomendados.addAll(servicioRepository.findByCategoriaAndPuntuacion(arte, 8L));
            serviciosRecomendados.addAll(servicioRepository.findByCategoriaAndPuntuacion(ciencia, 9L));
            serviciosRecomendados.addAll(servicioRepository.findByCategoriaAndPuntuacion(informatica, 4L));
            serviciosRecomendados.addAll(servicioRepository.findByCategoriaAndPuntuacion(geografia, 6L));
        } else if (edad >= 60 && edad < 80) {
            serviciosRecomendados.addAll(servicioRepository.findByCategoriaAndPuntuacionLessThan(arte, 9L));
            serviciosRecomendados.addAll(servicioRepository.findByCategoriaAndPuntuacionLessThan(ciencia, 10L));
            serviciosRecomendados.addAll(servicioRepository.findByCategoriaAndPuntuacionGreaterThan(informatica, 3L));
            serviciosRecomendados.addAll(servicioRepository.findByCategoriaAndPuntuacionLessThan(geografia, 7L));
        }

        return serviciosRecomendados;
    }


    public Recomendaciones obtenerRecomendaciones(Long usuarioId) 
    {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        int edad = calcularEdad(usuario.getFechaDeNacimiento());

        //List<Curso> cursosRecomendados = obtenerRecomendacionesCursos(edad);
        List<Servicio> serviciosRecomendados = obtenerRecomendacionesServicios(edad);

        return new Recomendaciones(/*cursosRecomendados, */serviciosRecomendados);
    }
}