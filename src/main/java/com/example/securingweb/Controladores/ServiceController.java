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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.securingweb.ORM.ficheros.Fichero;
import com.example.securingweb.ORM.ficheros.FicheroRepository;
import com.example.securingweb.ORM.servicios.categoria.Categoria;
import com.example.securingweb.ORM.servicios.categoria.CategoriaRepository;
import com.example.securingweb.ORM.servicios.muestras.Muestra;
import com.example.securingweb.ORM.servicios.muestras.MuestrasRepository;
import com.example.securingweb.ORM.servicios.opciones.Opcion;
import com.example.securingweb.ORM.servicios.opciones.OpcionRepository;
import com.example.securingweb.ORM.servicios.servicio.Servicio;
import com.example.securingweb.ORM.servicios.servicio.ServicioRepository;
import com.example.securingweb.ORM.servicios.valorarServicios.ValorarServicios;
import com.example.securingweb.ORM.servicios.valorarServicios.ValorarServiciosRepository;
import com.example.securingweb.ORM.usuario.Usuario;
import com.example.securingweb.ORM.usuario.UsuarioRepository;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.naming.NoPermissionException;

@Controller
@RequestMapping("/service")
public class ServiceController 
{
    private ServicioRepository servicioRepository;
    private CategoriaRepository categoriaRepository;
    private FicheroRepository ficheroRepository;
    private UsuarioRepository usuarioRepository;
    private OpcionRepository opcionRepository;
    private MuestrasRepository muestrasRepository;
    private ValorarServiciosRepository valorarServiciosRepository;


    @Autowired
    public ServiceController(ServicioRepository servicioRepository, CategoriaRepository categoriaRepository, FicheroRepository ficheroRepository, UsuarioRepository usuarioRepository, OpcionRepository opcionRepository, MuestrasRepository muestrasRepository, ValorarServiciosRepository valorarServiciosRepository)
    {
        this.servicioRepository = servicioRepository;
        this.categoriaRepository = categoriaRepository;
        this.ficheroRepository = ficheroRepository;
        this.usuarioRepository = usuarioRepository;
        this.opcionRepository = opcionRepository;
        this.muestrasRepository = muestrasRepository;
        this.valorarServiciosRepository = valorarServiciosRepository;
    }

    private Usuario getUser()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Usuario) authentication.getPrincipal();
    }

    @GetMapping({"", "/"})
    public String redirectList()
    {
        return "redirect:/service/list";
    }

    @GetMapping("create")
    public String getCreateService(Model modelo)
    {
        modelo.addAttribute("Servicio", new Servicio());
        modelo.addAttribute("Opcion", new Opcion());
        modelo.addAttribute("Muestra", new Muestra());
        return "createService";
    }
    
    @GetMapping("list")
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

    @GetMapping("view")
    public String serviceView(Model modelo, @RequestParam(value="title", required=false) String titulo)
    {
        if(titulo == null)
            return "redirect:/service/list";
        Optional<Servicio> solicitado = servicioRepository.findByTitulo(titulo.trim());
        if(solicitado.isEmpty())
            return "redirect:/error/404";
        modelo.addAttribute("servicio", solicitado.get());
        modelo.addAttribute("ValorarServicios", new ValorarServicios());
        return "serviceView";
    }

    @PostMapping("create")
    public String postCreateService(@ModelAttribute Servicio nuevo, @RequestParam(value="categoriaSeleccionada", required=true) String categoria, @RequestParam(value="portadaDireccion", required=true) String direccion)
    {
        Servicio guardar = new Servicio();

        guardar.setCreador(getUser());

        guardar.setFechaDeCreacion(new Date());
        System.out.println(new Date());

        Categoria catNueva = new Categoria();
        catNueva.setDescripcion(categoria);

        guardar.setCategoria(catNueva);

        guardar.setDescripcion(nuevo.getDescripcion().trim());

        Fichero portNueva = new Fichero();
        portNueva.setDireccion(direccion.trim());
        guardar.setPortada(portNueva);

        guardar.setTitulo(nuevo.getTitulo().trim());

        try
        {
            guardarServicio(guardar);
        }
        catch(DataIntegrityViolationException e)
        {
            return "redirect:/service/create?message=serviveExists";
        }
        catch(IllegalArgumentException e)
        {
            return "redirect:/service/create?message="+e.getMessage();
        }

        return "redirect:/service/view?title="+nuevo.getTitulo();
    }

    @GetMapping("delete")
    public String deleteService(@RequestParam(value="title", required = true) String titulo)
    {

        Optional<Servicio> aBorrar = servicioRepository.findByTitulo(titulo);

        if(aBorrar.isEmpty())
            return "redirect:/service/list?message=serviceNotFound";

        if (!aBorrar.get().getCreador().equals(getUser()))
            return "redirect:/error/403";

        servicioRepository.delete(aBorrar.get());

        return "redirect:/service/list";
    }

    @PostMapping("createOption")
    public String createOption(@ModelAttribute Opcion nuevo, @RequestParam(value="titulo", required=true) String padre, @RequestParam(value="precio", required=true) String precio)
    {
        nuevo.setPrecio(new BigDecimal(precio));
        try
        {
            guardarOpcion(nuevo, padre);
        }
        catch(IllegalArgumentException e)
        {
            return "redirect:/service/create?message="+e.getMessage();
        }
        catch(NoPermissionException e)
        {
            return "redirect:/error/403";
        }

        return "redirect:/service/view?title="+padre;
    }
    
    @GetMapping("deleteOption")
    public String deleteOption(@RequestParam(value="title", required = true) String title, @RequestParam(value="id", required = true) String id)
    {
        Optional<Opcion> aBorrar;
        try
        {
            aBorrar = opcionRepository.findById(Long.parseLong(id));
        }
        catch (NumberFormatException e)
        {
            return "redirect:/service/view?title="+title+"&message=optionNotDeleted";
        }

        if(aBorrar.isEmpty())
            return "redirect:/service/view?title="+title+"&message=optionNotFound";

        if(!aBorrar.get().getPadre().getCreador().equals(getUser()))
            return "redirect:/error/403";

        opcionRepository.delete(aBorrar.get());

        return "redirect:/service/view?title="+title+"&message=optionDeleted";
    }

    @PostMapping("createSample")
    public String createSample(@ModelAttribute Muestra nuevo, @RequestParam(value="titulo", required=true) String padre, @RequestParam(value="dirMultimedia", required=true) String dirMultimedia)
    {
        try
        {
            Fichero multimedia = new Fichero();
            multimedia.setDireccion(dirMultimedia.trim());
            nuevo.setFichero(multimedia);
            guardarMuestra(nuevo, padre);
        }
        catch(IllegalArgumentException e)
        {
            return "redirect:/service/create?message="+e.getMessage();
        }
        catch(NoPermissionException e)
        {
            return "redirect:/error/403";
        }
        return "redirect:/service/view?title="+padre;
    }

    @GetMapping("deleteSample")
    public String deleteSample(@RequestParam(value="title", required = true) String title, @RequestParam(value="id", required = true) String id)
    {
        Optional<Muestra> aBorrar;

        try
        {
            aBorrar = muestrasRepository.findById(Long.parseLong(id));
        }
        catch(NumberFormatException e)
        {
            return "redirect:/service/view?title="+title+"&message=sampleNotDeleted";
        }

        if(aBorrar.isEmpty())
            return "redirect:/service/view?title="+title+"&message=sampleNotDeleted";
    
        if(!aBorrar.get().getPadre().getCreador().equals(getUser()))
            return "redirect:/error/403";

        muestrasRepository.delete(aBorrar.get());

        return "redirect:/service/view?title="+title+"&message=sampleDeleted";
    }

    @PostMapping("rate")
    public String createRating(@ModelAttribute ValorarServicios valoracion, @RequestParam(value="title", required = true) String titulo)
    {

        Optional<Servicio> servicioValorado = servicioRepository.findByTitulo(titulo.trim());
        if(servicioValorado.isEmpty())
            return "redirect:/service/view?title="+titulo+"&message=noServiceFather";

        valoracion.setServicio(servicioValorado.get());
        valoracion.setUsuario(getUser());
        valoracion.setFecha(new Date());
        valoracion.setComentario(valoracion.getComentario().trim());

        valorarServiciosRepository.save(valoracion);

        return "redirect:/service/view?title="+titulo;
    }

    @GetMapping("deleteRating")
    public String deleteRating(@RequestParam(value="title", required = true) String title, @RequestParam(value="id", required = true) String id)
    {
        Optional<ValorarServicios> aBorrar;

        try
        {
            aBorrar = valorarServiciosRepository.findById(Long.parseLong(id));
        }
        catch(NumberFormatException e)
        {
            return "redirect:/service/view?title="+title+"&message=ratingNotDeleted";
        }

        if(aBorrar.isEmpty())
            return "redirect:/service/view?title="+title+"&message=ratingNotDeleted";

        if(!aBorrar.get().getUsuario().equals(getUser()))
        {
            System.out.println("Usuario ACTUAL: " + getUser().getUsername());
            System.out.println("Usuario VALORADOR: " + aBorrar.get().getUsuario().getUsername());
            return "redirect:/error/403";
        }

        valorarServiciosRepository.delete(aBorrar.get());

        return "redirect:/service/view?title="+title+"&message=ratingDeleted";
    }

    public Servicio guardarServicio(Servicio servicio) 
    {
        if (servicio == null || servicio.getCreador() == null || servicio.getCategoria() == null || servicio.getPortada() == null) {
            throw new IllegalArgumentException("notEnoughData");
        }
        servicio.getPortada().setDireccion(servicio.getPortada().getDireccion().trim());
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
            throw new IllegalArgumentException("noServiceCreator");
        }
    
        servicio.setCreador(creadorExistente.get());
        servicio.setCategoria(categoria);
        servicio.setPortada(portada);
        servicio.setTitulo(servicio.getTitulo().trim());
        return servicioRepository.save(servicio);
    }
    
    public Opcion guardarOpcion(Opcion nuevo, String tituloPadre) throws NoPermissionException
    {
        Optional<Servicio> padreEncotrado = servicioRepository.findByTitulo(tituloPadre.trim());

        if(padreEncotrado.isEmpty())
            throw new IllegalArgumentException("noServiceFather");

        if(!padreEncotrado.get().getCreador().equals(getUser()))
            throw new NoPermissionException();

        nuevo.setPadre(padreEncotrado.get());
        nuevo.setDescripcion(nuevo.getDescripcion().trim());

        return opcionRepository.save(nuevo);
    }

    public Muestra guardarMuestra(Muestra nuevo, String tituloPadre) throws NoPermissionException
    {        
        Optional<Servicio> padreEncotrado = servicioRepository.findByTitulo(tituloPadre.trim());

        if(padreEncotrado.isEmpty())
            throw new IllegalArgumentException("noServiceFather");

        if(!padreEncotrado.get().getCreador().equals(getUser()))
            throw new NoPermissionException();

        nuevo.setPadre(padreEncotrado.get());
        nuevo.getMultimedia().setDireccion(nuevo.getMultimedia().getDireccion().trim());
        Optional<Fichero> ficheroEncontrado = ficheroRepository.findByDireccion(nuevo.getMultimedia().getDireccion());
        if(ficheroEncontrado.isEmpty())
            nuevo.setFichero(ficheroRepository.save(nuevo.getMultimedia()));
        else
            nuevo.setFichero(ficheroEncontrado.get());

        return muestrasRepository.save(nuevo);
    }
}