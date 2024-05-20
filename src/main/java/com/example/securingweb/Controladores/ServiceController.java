package com.example.securingweb.Controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
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
import com.example.securingweb.ORM.servicios.opciones.Opcion;
import com.example.securingweb.ORM.servicios.opciones.OpcionRepository;
import com.example.securingweb.ORM.servicios.servicio.Servicio;
import com.example.securingweb.ORM.servicios.servicio.ServicioRepository;
import com.example.securingweb.ORM.usuario.Usuario;
import com.example.securingweb.ORM.usuario.UsuarioRepository;
import java.math.BigDecimal;
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
    private OpcionRepository opcionRepository;


    @Autowired
    public ServiceController(ServicioRepository servicioRepository, CategoriaRepository categoriaRepository, FicheroRepository ficheroRepository, UsuarioRepository usuarioRepository, OpcionRepository opcionRepository)
    {
        this.servicioRepository = servicioRepository;
        this.categoriaRepository = categoriaRepository;
        this.ficheroRepository = ficheroRepository;
        this.usuarioRepository = usuarioRepository;
        this.opcionRepository = opcionRepository;
    }


    @GetMapping("createService")
    public String getCreateService(Model modelo)
    {
        modelo.addAttribute("Servicio", new Servicio());
        modelo.addAttribute("Opcion", new Opcion());
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

        try
        {
            guardarServicio(guardar);
        }
        catch(DataIntegrityViolationException e)
        {
            return "redirect:/createService?exists";
        }

        return "redirect:/snake";
    }

    @PostMapping("createOption")
    public String postCreateOption(@ModelAttribute Opcion nuevo, @RequestParam(value="titulo", required=true) String padre, @RequestParam(value="precio", required=true) String precio)
    {
        nuevo.setPrecio(new BigDecimal(precio));
        try
        {
            guardarOpcion(nuevo, padre);
        }
        catch(IllegalArgumentException e)
        {
            return "redirect:/createService?"+e.getMessage();
        }

        return "redirect:/snake";
    }
    
    @GetMapping("serviceList")
    public String serviceList(Model modelo, @RequestParam(value="page", required=false, defaultValue = "0") String page, @RequestParam(value="size", required=false, defaultValue = "9") String size)
    {
        int pageInt = 0;
        int sizeInt = 9;
        try
        {
            pageInt = Integer.parseInt(page);
        }
        catch(Exception e)
        {}

        try
        {
            sizeInt = Integer.parseInt(size);
        }
        catch(Exception e)
        {}

        List<Servicio> lista = servicioRepository.findAll(PageRequest.of(pageInt, sizeInt)).getContent();
        modelo.addAttribute("servicios", lista);

        return "serviceList";
    }

    @GetMapping("service")
    public String serviceView(Model modelo, @RequestParam(value="title", required=true) String titulo)
    {
        Servicio solicitado = servicioRepository.findByTitulo(titulo);
        if(solicitado == null)
            return "error/404";
        modelo.addAttribute("servicio", solicitado);
        return "serviceView";
    }

    public Servicio guardarServicio(Servicio servicio) {
        if (servicio == null || servicio.getCreador() == null || servicio.getCategoria() == null || servicio.getPortada() == null) {
            throw new IllegalArgumentException("null");
        }
    
        Optional<Fichero> ficheroExistente = ficheroRepository.findByDireccion(servicio.getPortada().getDireccion());
        Fichero portada;
        if (ficheroExistente.isEmpty()) {
            portada = ficheroRepository.save(servicio.getPortada());
        } else {
            portada = ficheroExistente.get();
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
            throw new IllegalArgumentException("noCreator");
        }
    
        servicio.setCreador(creadorExistente.get());
        servicio.setCategoria(categoria);
        servicio.setPortada(portada);
        servicio.setTitulo(servicio.getTitulo().trim());
        return servicioRepository.save(servicio);
    }
    
    public Opcion guardarOpcion(Opcion nuevo, String tituloPadre)
    {
        Servicio padreEncotrado = servicioRepository.findByTitulo(tituloPadre);

        if(padreEncotrado == null)
            throw new IllegalArgumentException("noFather");

        nuevo.setPadre(padreEncotrado);

        return opcionRepository.save(nuevo);
    }
}