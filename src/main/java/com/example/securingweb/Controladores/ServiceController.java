package com.example.securingweb.Controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.securingweb.ORM.ficheros.Fichero;
import com.example.securingweb.ORM.ficheros.FicheroRepository;
import com.example.securingweb.ORM.servicios.categoria.Categoria;
import com.example.securingweb.ORM.servicios.categoria.CategoriaRepository;
import com.example.securingweb.ORM.servicios.servicio.Servicio;
import com.example.securingweb.ORM.servicios.servicio.ServicioRepository;
import com.example.securingweb.ORM.usuario.Usuario;
import com.example.securingweb.ORM.usuario.UsuarioRepository;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
public class ServiceController 
{
    private ServicioRepository servicioRepository;
    private CategoriaRepository categoriaRepository;
    private FicheroRepository ficheroRepository;
    private UsuarioRepository usuarioRepository;


    @Autowired
    public ServiceController(ServicioRepository servicioRepository, CategoriaRepository categoriaRepository, FicheroRepository ficheroRepository, UsuarioRepository usuarioRepository)
    {
        this.servicioRepository = servicioRepository;
        this.categoriaRepository = categoriaRepository;
        this.ficheroRepository = ficheroRepository;
        this.usuarioRepository = usuarioRepository;
    }


    @GetMapping("createService")
    public String getCreateService(Model modelo)
    {
        modelo.addAttribute("Servicio", new Servicio());
        return "createService";
    }
    
    @PostMapping("createService")
    public String postCreateService(@ModelAttribute Servicio nuevo, @RequestParam(value="categoriaSeleccionada", required=true) String categoria, @RequestParam(value="portadaDireccion", required=true) String direccion)
    {

        Servicio guardar = new Servicio();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        guardar.setCreador((Usuario) authentication.getPrincipal());

        guardar.setFechaDeCreacion(new Date());
        System.out.println(new Date());

        Categoria catNueva = new Categoria();
        catNueva.setDescripcion(categoria);

        guardar.setCategoria(catNueva);

        guardar.setDescripcion(nuevo.getDescripcion());

        Fichero portNueva = new Fichero();
        portNueva.setDireccion(direccion);
        guardar.setPortada(portNueva);

        guardar.setTitulo(nuevo.getTitulo());

        guardarServicio(guardar);

        return "redirect:/snake";
    }

    
    public List<Servicio> obtenerTodosLosServicios() 
    {
        return servicioRepository.findAll();
    }

    public Servicio guardarServicio(Servicio servicio) {
        if (servicio == null || servicio.getCreador() == null || servicio.getCategoria() == null || servicio.getPortada() == null) {
            throw new IllegalArgumentException("Servicio, creador, categoría y portada no deben ser nulos");
        }
    
        Fichero ficheroExistente = ficheroRepository.findByDireccion(servicio.getPortada().getDireccion());
        Fichero portada;
        if (ficheroExistente == null) {
            portada = ficheroRepository.save(servicio.getPortada());
        } else {
            portada = ficheroExistente;
        }
    
        Optional<Categoria> categoriaExistente = categoriaRepository.findByDescripcion(servicio.getCategoria().getDescripcion());
        Categoria categoria;
        if (categoriaExistente.isPresent()) {
            categoria = categoriaExistente.get();
        } else {
            categoria = categoriaRepository.save(servicio.getCategoria());
        }
    
        Optional<Usuario> creadorExistente = usuarioRepository.findById(servicio.getCreador().getId());
        if (creadorExistente.isEmpty()) {
            throw new IllegalArgumentException("El creador no existe");
        }
    
        servicio.setCreador(creadorExistente.get());
        servicio.setCategoria(categoria);
        servicio.setPortada(portada);
        return servicioRepository.save(servicio);
    }
    
}
