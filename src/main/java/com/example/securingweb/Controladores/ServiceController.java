package com.example.securingweb.Controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.example.securingweb.ORM.ficheros.Fichero;
import com.example.securingweb.ORM.ficheros.FicheroRepository;
import com.example.securingweb.ORM.ficheros.FicheroService;
import com.example.securingweb.ORM.idiomas.Idioma;
import com.example.securingweb.ORM.idiomas.IdiomaRepository;
import com.example.securingweb.ORM.servicios.categoria.Categoria;
import com.example.securingweb.ORM.servicios.categoria.CategoriaRepository;
import com.example.securingweb.ORM.servicios.comprarServicios.ComprarServicio;
import com.example.securingweb.ORM.servicios.comprarServicios.ComprarServiciosRepository;
import com.example.securingweb.ORM.servicios.muestras.Muestra;
import com.example.securingweb.ORM.servicios.muestras.MuestrasRepository;
import com.example.securingweb.ORM.servicios.opciones.Opcion;
import com.example.securingweb.ORM.servicios.opciones.OpcionRepository;
import com.example.securingweb.ORM.servicios.servicio.Servicio;
import com.example.securingweb.ORM.servicios.servicio.ServicioRepository;
import com.example.securingweb.ORM.servicios.valorarServicios.ValorarServicios;
import com.example.securingweb.ORM.servicios.valorarServicios.ValorarServiciosRepository;
import com.example.securingweb.ORM.usuarios.usuario.Usuario;
import com.example.securingweb.ORM.usuarios.usuario.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import javax.naming.NoPermissionException;

@Controller
@RequestMapping("/service")
public class ServiceController 
{
    private ServicioRepository servicioRepository;
    private CategoriaRepository categoriaRepository;
    private FicheroRepository ficheroRepository;
    private FicheroService ficheroService;
    private UsuarioRepository usuarioRepository;
    private OpcionRepository opcionRepository;
    private MuestrasRepository muestrasRepository;
    private ValorarServiciosRepository valorarServiciosRepository;
    private IdiomaRepository idiomaRepository;
    private ComprarServiciosRepository comprarServiciosRepository;
    private WebSocketController webSocketController;

    @Autowired
    public ServiceController(ServicioRepository servicioRepository, 
                        CategoriaRepository categoriaRepository, 
                        FicheroRepository ficheroRepository, 
                        FicheroService ficheroService,
                        UsuarioRepository usuarioRepository, 
                        OpcionRepository opcionRepository, 
                        MuestrasRepository muestrasRepository, 
                        ValorarServiciosRepository valorarServiciosRepository, 
                        IdiomaRepository idiomaRepository,
                        ComprarServiciosRepository comprarServiciosRepository,
                        WebSocketController webSocketController)
    {
        this.servicioRepository = servicioRepository;
        this.categoriaRepository = categoriaRepository;
        this.ficheroRepository = ficheroRepository;
        this.ficheroService = ficheroService;
        this.usuarioRepository = usuarioRepository;
        this.opcionRepository = opcionRepository;
        this.muestrasRepository = muestrasRepository;
        this.valorarServiciosRepository = valorarServiciosRepository;
        this.idiomaRepository = idiomaRepository;
        this.comprarServiciosRepository = comprarServiciosRepository;
        this.webSocketController = webSocketController;
    }

    /**
     * 
     * @return el usuario loggeado
     */
    private Usuario getUser()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Usuario) (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof Usuario) ? null : authentication.getPrincipal());
    }

    /**
     * Devuelve el servicio con el nombre <code>title</code> o <code>null</code> si no existe el servicio
     * @param title
     * @return <code>Servicio</code> con nombre <code>title</code> o <code>null</null>
     */
    private Servicio checkTitle(String title) 
    {
        return Optional.of(servicioRepository.findByTitulo(title).get()).orElseGet(() -> null);
    }

    private String encode(String str)
    {
        try
        {
            return URLEncoder.encode(str, StandardCharsets.UTF_8.toString());
        }
        catch (UnsupportedEncodingException e)
        {
            return " ";
        }
    }

    @GetMapping({"", "/"})
    public String redirectList()
    {
        return "redirect:/service/list";
    }

    /**
     * Solo carga la pantalla con el formulario de creacion de servicios
     */
    @GetMapping("create")
    public String getCreateService(Model modelo)
    {
        modelo.addAttribute("Servicio", new Servicio());
        return "service/createService"; //pagina
    }
    
    @GetMapping("list")
    public String serviceList(Model modelo, 
                            @RequestParam(value="page", required=false, defaultValue = "0") String page, 
                            @RequestParam(value="size", required=false, defaultValue = "9") String size, 
                            HttpServletRequest request)
    {
        int pageInt = 0; //page indica que página se ha cargado
        int sizeInt = 9; //size indica cuantos servicios se cargan en cada página

        Page<Servicio> lista;
        try
        {
            pageInt = Integer.parseInt(page);
        }
        catch(Exception e)
        {} //Si da error no hacer nada para usar los valores por defecto

        try
        {
            sizeInt = Integer.parseInt(size);
        }
        catch(Exception e)
        {} //Si da error no hacer nada para usar los valores por defecto

        if(sizeInt <= 0)
            sizeInt = 1;

        if(pageInt < 0)
            pageInt = 0;

        Locale locale = RequestContextUtils.getLocale(request); // Obtener el idioma actual de la sesión
        Optional<Idioma> idiomOptional = idiomaRepository.findByIdioma(locale.getLanguage());

        if(idiomOptional.isEmpty()) //Si no se ha encontrado el idioma pedido en la DB, cargar todos los servicios
            lista = servicioRepository.findAllByPublicado(true, PageRequest.of(pageInt, sizeInt));
        else
            lista = servicioRepository.findAllByIdioma_idAndPublicado(idiomOptional.get().getId(), true, PageRequest.of(pageInt, sizeInt));
        
        //Cargar todos los atributos necesarios para cargar todo correctamente
        modelo.addAttribute("servicios", lista.getContent());
        modelo.addAttribute("haySiguiente", lista.hasNext());
        modelo.addAttribute("hayAnterior", lista.hasPrevious());
        modelo.addAttribute("pagActual", pageInt);

        return "service/serviceList"; //pagina
    }

    @GetMapping("view")
    public String serviceView(Model modelo, @RequestParam(value="title", required=false) String titulo)
    {
        if(titulo == null)
            return "redirect:/";

        Optional<Servicio> solicitado = servicioRepository.findByTitulo(titulo.trim()); //.trim() sirve para quitar los espacios en blanco al principio y al final de los strings

        if(solicitado.isEmpty()) //Esto significa que el servicio pedido no se ha encontrado en la base de datos
            return "redirect:/error/404"; //404: no encontrado

        if(!solicitado.get().getPublicado()) //Si no está publicado, no mostrarlo. Esto es necesario por si se introduce manualmente en la url el nombre del servicio
            return "redirect:/error/404";

        modelo.addAttribute("servicio", solicitado.get());
        modelo.addAttribute("admin", getUser() == null ? false : getUser().isAdmin());
        modelo.addAttribute("usuarioID", getUser() == null ? -1 : getUser().getId());
        modelo.addAttribute("ValorarServicios", new ValorarServicios());
        return "service/serviceView"; //pagina
    }

    @PostMapping("create")
    public String postCreateService(@ModelAttribute Servicio nuevo, 
                                @RequestParam(value="categoriaParam", required=true) String categoria, 
                                @RequestParam(value="idiomaParam", required=true) String idioma,
                                @RequestParam(value="file", required=true) MultipartFile file) throws IOException
    {
        Servicio guardar = new Servicio();

        if(getUser() == null)
            return "redirect:/error/403";

        guardar.setCreador(getUser());
        guardar.setFechaDeCreacion(new Date());

        Categoria catNueva = new Categoria();
        catNueva.setDescripcion(categoria);
        guardar.setCategoria(catNueva);

        guardar.setDescripcion(nuevo.getDescripcion().trim());
        guardar.setPublicado(false);
        guardar.setVerificado(false);
        guardar.setPuntuacion(0L);

        Optional<Idioma> idiomaEncontrado = idiomaRepository.findByIdioma(idioma);
        if (idiomaEncontrado.isEmpty()) {
            return "redirect:/error";
        }
        guardar.setIdioma(idiomaEncontrado.get());

        guardar.setTitulo(nuevo.getTitulo().trim());


        try 
        {
            if(!ficheroService.isImg(file))
                return "redirect:/service/create?messagee=invalidFile";
            guardar = guardarServicio(guardar); //Guardar el servicio primero sin la portada, ya que todavía no existe el ID del servicio
            guardar.setPortada(ficheroRepository.save(ficheroService.crearFicheroServicio(file, guardar, "portada"))); //Guardar el fichero en el sistema y en la DB
            servicioRepository.save(guardar); //Guardar el servicio con la portada en la DB
        }
        catch (DataIntegrityViolationException e)
        {
            return "redirect:/service/create?message=serviceExists";
        }
        catch (IllegalArgumentException e)
        {
            return "redirect:/service/create?message=" + e.getMessage();
        }
        catch (IOException e)
        {
            return "redirect:/service/create?message=fileError";
        }

        return "redirect:/service/edit?title=" + encode(nuevo.getTitulo()) + "&message=serviceCreated";
    }


    @GetMapping("delete")
    public String deleteService(@RequestParam(value="title", required = true) String titulo)
    {

        Optional<Servicio> aBorrar = servicioRepository.findByTitulo(titulo);

        if(aBorrar.isEmpty())
            return "redirect:/user/services?message=serviceNotFound";

        if (!aBorrar.get().getCreador().equals(getUser()) && !getUser().isAdmin())
            return "redirect:/error/403";

        try
        {
            ficheroService.borrarFichero(aBorrar.get().getPortada());
            ficheroRepository.delete(aBorrar.get().getPortada());
            for(Muestra i : aBorrar.get().getMuestras())
            {
                ficheroService.borrarFichero(i.getMultimedia());
                ficheroRepository.delete(i.getMultimedia());
            }
        }
        catch(IOException e)
        {
            return "redirect:/service/edit?title="+encode(titulo)+"&message=fileError";
        }

        servicioRepository.delete(aBorrar.get());

        return "redirect:/user/services?message=serviceDeleted"; //pagina
    }

    @PostMapping("publish")
    public String publishService(@RequestParam(value="title", required = true) String titulo,
                                 @RequestParam(value = "private", required = true) String isPrivate)
    {
        Optional<Servicio> guardado = servicioRepository.findByTitulo(titulo);
        if(guardado.isEmpty())
            return "redirect:/user/services?message=serviceNotFound";

        if(!guardado.get().getCreador().equals(getUser()) && !getUser().isAdmin())
            return "redirect:/error/403";

        guardado.get().setPublicado(isPrivate.equals("y") ? false : true);
        servicioRepository.save(guardado.get());
        return "redirect:/service/edit?title="+encode(guardado.get().getTitulo())+"&message="+(isPrivate.equals("y")? "servicePrivated" : "servicePublished");
    }

    @GetMapping("edit")
    public String updateService(Model modelo, @RequestParam(value="title", required = true)String titulo)
    {
        Optional<Servicio> aEditar = servicioRepository.findByTitulo(titulo);

        if(aEditar.isEmpty())
            return "redirect:/user/services?message=serviceNotFound";

        if(!aEditar.get().getCreador().equals(getUser()) && !getUser().isAdmin())
            return "redirect:/error/403";

        modelo.addAttribute("Servicio", aEditar.get());
        modelo.addAttribute("ListaOpciones", aEditar.get().getOpciones());
        modelo.addAttribute("ListaMuestras", aEditar.get().getMuestras());
        modelo.addAttribute("Opcion", new Opcion());
        return "service/editService"; //pagina
    }

    @PostMapping("edit")
    public String PostUpdateService(@ModelAttribute Servicio nuevo, 
                                @RequestParam(value="tituloViejo", required=true) String tituloViejo, 
                                @RequestParam(value="categoriaParam", required=true) String categoriaParam, 
                                @RequestParam(value="idiomaParam", required=true) String idiomaParam,
                                @RequestParam(value="file", required=false) MultipartFile file) throws IOException 
    {
        Optional<Servicio> guardadoOpt = servicioRepository.findByTitulo(tituloViejo);
        

        if (guardadoOpt.isEmpty())
            return "redirect:/user/services?message=serviceNotFound";

        Servicio guardado = guardadoOpt.get();

        if (!guardado.getCreador().equals(getUser()) && !getUser().isAdmin())
            return "redirect:/error/403";
        
        if(!guardado.getTitulo().equals(nuevo.getTitulo()) && servicioRepository.findByTitulo(nuevo.getTitulo()).isPresent())
            return "redirect:/service/edit?title="+encode(tituloViejo)+"&message=serviceExists";

        guardado.setDescripcion(nuevo.getDescripcion());
        guardado.setFechaDeActualizacion(new Date());
        
        if (!guardado.getCategoria().getDescripcion().equals(categoriaParam)) { //Comprobar si se ha cambiado la categoría
            Optional<Categoria> cat = Optional.ofNullable(categoriaRepository.findByDescripcion(categoriaParam));
            if (cat.isEmpty()) {
                Categoria catNueva = new Categoria();
                catNueva.setDescripcion(categoriaParam);
                guardado.setCategoria(categoriaRepository.save(catNueva));
            } else {
                guardado.setCategoria(cat.get());
            }
        }
        
        if (!guardado.getIdioma().getIdioma().equals(idiomaParam)) { //Comprobar si se ha cambiado el idioma
            Optional<Idioma> idi = idiomaRepository.findByIdioma(idiomaParam);
            if (idi.isEmpty()) {
                Idioma idiNuevo = new Idioma();
                idiNuevo.setIdioma(idiomaParam);
                guardado.setIdioma(idiomaRepository.save(idiNuevo));
            } else {
                guardado.setIdioma(idi.get());
            }
        }
        
        Optional<Fichero> ficheroGuardado = ficheroRepository.findById(guardado.getPortada().getId());
        
        if(!file.isEmpty())
        {
            try
            {
                if(!ficheroService.isImg(file))
                    return "redirect:/service/edit?title="+encode(guardado.getTitulo())+"&message=invalidFile";
                ficheroService.cambiarFicheroServicio(file, guardado, "portada", ficheroGuardado.get());
                ficheroRepository.save(ficheroGuardado.get());
            }
            catch(Exception e)
            {
                e.printStackTrace();
                return "redirect:/service/edit?title="+encode(guardado.getTitulo())+"&message=fileError";
            }
        }
        
        guardado.setPortada(ficheroGuardado.get());
        guardado.setVerificado(false);
        guardado.setPuntuacion(0L);
        guardado.setTitulo(nuevo.getTitulo());


        servicioRepository.save(guardado);

        return "redirect:/service/edit?title="+encode(guardado.getTitulo())+"&message=serviceEdited";
    }

    @PostMapping("buy") //TODO: QUE SE COMPRE CON EL PROCEDIMIENTO
    public String buyService(@RequestParam(value="title", required = true) String padre,
                            @RequestParam(value="id", required = true) String id)
    {
        Optional<Servicio> servicio = servicioRepository.findByTitulo(padre);

        if(servicio.isEmpty())
            return "redirect:/service/list?message=serviceNotFound";

        Optional<Opcion> opcion;
        try
        {
            opcion = opcionRepository.findById(Long.parseLong(id));
        }
        catch(NumberFormatException e)
        {
            return "redirect:/service/view?title="+encode(padre)+"&message=serviceNotBought";
        }

        if(opcion.isEmpty())
            return "redirect:/service/view?title="+encode(padre)+"&message=optionNotFound";

        Usuario user = getUser();
        if(user == null)
            return "redirect:/error/403";

        if(user.isAdmin())
            return "redirect:/error/403";

        if(user.equals(servicio.get().getCreador()))
            return "redirect:/service/view?title="+encode(padre)+"&message=cannotBuyOwnService";

        if(getUser().getSaldo().compareTo(opcion.get().getPrecio()) < 0)
            return "redirect:/service/view?title="+encode(padre)+"&message=noEnoughBalance";
        
        user.setSaldo(getUser().getSaldo().subtract(opcion.get().getPrecio()));
        usuarioRepository.save(user);

        ComprarServicio compra = new ComprarServicio();
        compra.setFecha(new Date());
        compra.setOpcionCompra(opcion.get());
        compra.setServicio(servicio.get());
        compra.setUsuario(user);
        compra.setTerminado(false);
        comprarServiciosRepository.save(compra);

        return "redirect:/snake"; //pagina //MARK: esto debería redirigir al chat o algo así ns
    }

    @PostMapping("createOption")
    public String createOption(@ModelAttribute Opcion nuevo, 
                            @RequestParam(value="title", required=true) String padre, 
                            @RequestParam(value="precio", required=true) String precio)
    {
        nuevo.setPrecio(new BigDecimal(precio)); //BigDecimal ya que en la base de datos se guarda como un DECIMAL
        nuevo.setDescripcion(nuevo.getDescripcion().trim());
        try
        {
            guardarOpcion(nuevo, padre);
        }
        catch(IllegalArgumentException e) //el titulo del servicio que se ha pasado no existe
        {
            return "redirect:/user/services?message="+e.getMessage();
        }
        catch(NoPermissionException e)
        {
            return "redirect:/error/403";
        }

        return "redirect:/service/edit?title="+encode(padre)+"&message=optionCreated";
    }
    
    @GetMapping("deleteOption")
    public String deleteOption(@RequestParam(value="title", required = true) String title,
                            @RequestParam(value="id", required = true) String id)
    {
        Optional<Opcion> aBorrar;
        if(checkTitle(title) == null)
            return "redirect:/user/services?message=noServiceFather";
        
        try
        {
            aBorrar = opcionRepository.findById(Long.parseLong(id));
        }
        catch (NumberFormatException e)
        {
            return "redirect:/service/edit?title="+encode(title)+"&message=optionNotDeleted";
        }
        
        if(aBorrar.isEmpty())
            return "redirect:/service/edit?title="+encode(title)+"&message=optionNotFound";
        
        if(!aBorrar.get().getPadre().getCreador().equals(getUser()) && !getUser().isAdmin())
            return "redirect:/error/403";

        opcionRepository.delete(aBorrar.get());

        return "redirect:/service/edit?title="+encode(title)+"&message=optionDeleted";
    }

    @PostMapping("editOption")
    public String updateOption(@RequestParam(value = "id", required = true) String id, 
                            @RequestParam(value="title", required = true) String title, 
                            @RequestParam(value = "desc", required = true) String desc, 
                            @RequestParam(value = "precio", required = true) String precio)
    {
        Servicio servicio = checkTitle(title);
        if(servicio == null)
            return "redirect:/user/services?message=noServiceFather";
            
        Optional<Opcion> guardado;
        try
        {
            guardado= opcionRepository.findById(Long.parseLong(id));
        }
        catch (NumberFormatException e)
        {
            return "redirect:/service/edit?title="+encode(title)+"&message=optionNotEdited";
        }

        if(guardado.isEmpty())
            return "redirect:/service/edit?title="+encode(title)+"&message=optionNotFound";

        if(!guardado.get().getPadre().getCreador().equals(getUser()) && !getUser().isAdmin())
            return "redirect:/error/403";

        guardado.get().setPrecio(new BigDecimal(precio));
        guardado.get().setDescripcion(desc.trim());

        try
        {
            guardarOpcion(guardado.get(), title);
            servicio.setVerificado(false);
            servicio.setPuntuacion(0L);
            servicioRepository.save(servicio);
        }
        catch(IllegalArgumentException e)
        {
            return "redirect:/user/services?message="+e.getMessage();
        }
        catch(NoPermissionException e)
        {
            return "redirect:/error/403";
        }

        return "redirect:/service/edit?title="+encode(title)+"&message=optionEdited";
    }

    @PostMapping("createSample")
    public String createSample(@RequestParam(value="title", required=true) String padre, 
                            @RequestParam(value="file", required=true) MultipartFile file, 
                            @RequestParam(value="pos", required=true) String pos)
    {
        Long posLong;
        Servicio servicio = checkTitle(padre);
        Muestra nuevo = new Muestra();
        if(servicio == null)
            return "redirect:/user/services?message=noServiceFather";

        if(!servicio.getCreador().equals(getUser()))
            return "redirect:/error/403";

        try
        {
            posLong = Long.parseLong(pos);
        }
        catch (NumberFormatException e)
        {
            return "redirect:/service/edit?title="+encode(padre)+"&message=sampleNotCreated";
        }

        try
        {
            if(!ficheroService.isImg(file) && !ficheroService.isVid(file))
                return "redirect:/service/edit?title="+encode(padre)+"&message=invalidFile";
            
            nuevo.setMultimedia(ficheroService.crearFicheroServicio(file, servicio, "muestra"+pos));
            ficheroRepository.save(nuevo.getMultimedia());
            nuevo.setPosicion(posLong);
            nuevo.setPadre(servicio);
            muestrasRepository.save(nuevo);

            servicio.setVerificado(false);
            servicio.setPuntuacion(0L);
            servicioRepository.save(servicio);
        }
        catch(IOException e)
        {
            return "redirect:/service/edit?title="+encode(padre)+"&message=fileError";
        }
        return "redirect:/service/edit?title="+encode(padre)+"&message=sampleCreated";
    }

    @GetMapping("deleteSample")
    public String deleteSample(@RequestParam(value="title", required = true) String title, 
                            @RequestParam(value="id", required = true) String id)
    {
        Optional<Muestra> aBorrar;

        if(checkTitle(title) == null)
            return "redirect:/user/services?message=noServiceFather";
            
        try
        {
            aBorrar = muestrasRepository.findById(Long.parseLong(id));
        }
        catch(NumberFormatException e)
        {
            return "redirect:/service/edit?title="+encode(title)+"&message=sampleNotDeleted";
        }
        
        if(aBorrar.isEmpty())
            return "redirect:/service/edit?title="+encode(title)+"&message=sampleNotDeleted";
        
        if(!aBorrar.get().getPadre().getCreador().equals(getUser()) && !getUser().isAdmin())
            return "redirect:/error/403";
        try
        {
            ficheroService.borrarFichero(aBorrar.get().getMultimedia());
            muestrasRepository.delete(aBorrar.get());
            ficheroRepository.delete(aBorrar.get().getMultimedia());
        }
        catch (IOException e)
        {
            return "redirect:/service/edit?title="+encode(title)+"&message=fileError";
        }
        
        List<Muestra> lista = checkTitle(title).getMuestras(); //Se usa checkTitle para cargar la lista de todas las muesras qua hay despues de borrar la muestra deseada
        if(!lista.isEmpty())
        {
            Long anterior = (long)0;
            try
            {
                for(Muestra i : lista)
                {
                    System.out.println("Posicion del guardado: " + i.getPosicion() + " , anterior+1: " + (anterior+1));
                    if(!i.getPosicion().equals(anterior+1)) //Esto comprueba que todas las muestras tengan los números seguidos, ya que al borrar una muestra que no sea la última se rompe la cadena de números
                    {
                        i.setPosicion(anterior+1); //Ponerle el número siguiente al anterior para reestablecer la cadena de números
                        ficheroRepository.save(ficheroService.cambiarNombreFichero(i.getMultimedia(), "muestra"+(anterior+1)));
                        System.out.println("Se le va a poner el nombre: " + "muestra"+(anterior+1));
                        muestrasRepository.save(i); //Hay que guardar la muestra de nuevo para actualizarla con su nueva posición
                    }
                    anterior++;
                }
            }
            catch(IOException e)
            {
                e.printStackTrace();
                return "redirect:/service/edit?title="+encode(title)+"&message=fileError";
            }
        }

        return "redirect:/service/edit?title="+encode(title)+"&message=sampleDeleted";
    }

    @PostMapping("editSample") //En /editSample NO se cambia la posición de la muestra, para eso está /editSamplePos
    public String editSample(@RequestParam(value="id", required = true) String id, 
                        @RequestParam(value = "title", required = true) String titulo, 
                        @RequestParam(value="file", required=true) MultipartFile file)
    {
        Servicio servicio = checkTitle(titulo);
        if(servicio == null)
            return "redirect:/user/services?message=noServiceFather";
            
        Optional<Muestra> guardado;

        try
        {
            guardado = muestrasRepository.findById(Long.parseLong(id));
        }
        catch(NumberFormatException e)
        {
            return "redirect:/service/view?title="+encode(titulo)+"&message=sampleNotEdited";
        }

        if(guardado.isEmpty())
            return "redirect:/service/view?title="+encode(titulo)+"&message=sampleNotFound";

        if(!guardado.get().getPadre().getCreador().equals(getUser()) && !getUser().isAdmin())
            return "redirect:/error/403";

        Optional<Fichero> fichero = ficheroRepository.findByDireccion(guardado.get().getMultimedia().getDireccion());
        if(fichero.isEmpty())
            return "redirect:/service/edit?title="+encode(titulo)+"&message=fileNotFound";
        
        try
        {
            if(!ficheroService.isImg(file) && !ficheroService.isVid(file))
                return "redirect:/service/edit?title="+encode(titulo)+"&message=invalidFile";
            ficheroService.cambiarFicheroServicio(file, servicio, "muestra"+guardado.get().getPosicion(), fichero.get());
        }
        catch(IOException e)
        {
            return "redirect:/service/edit?title="+encode(titulo)+"&message=fileError";
        }

        muestrasRepository.save(guardado.get());

        servicio.setVerificado(false);
        servicio.setPuntuacion(0L);
        servicioRepository.save(servicio);

        return "redirect:/service/edit?title="+encode(guardado.get().getPadre().getTitulo())+"&message=sampleEdited";
    }

    @GetMapping("editSamplePos")
    public String editSamplePos(@RequestParam(value="id", required = true) String id, 
                            @RequestParam(value = "title", required = true) String titulo, 
                            @RequestParam(value = "dir", required = true) String dir)
    {
        Servicio servicio = checkTitle(titulo);
        if(servicio == null)
            return "redirect:/user/services?message=noServiceFather";
            
        Optional<Muestra> guardado;
        Muestra otro = null;

        if(!servicio.getCreador().equals(getUser()) && !getUser().isAdmin())
            return "redirect:/error/403";
        
        try
        {
            guardado = muestrasRepository.findById(Long.parseLong(id));
        }
        catch(NumberFormatException e)
        {
            return "redirect:/service/edit?title="+encode(titulo)+"&message=sampleNotEdited";
        }

        if(guardado.isEmpty())
            return "redirect:/service/edit?title="+encode(titulo)+"&message=sampleNotFound";

        List<Muestra> lista = servicio.getMuestras(); //Cargar la lista de muestras (están ordenadas por su posición, no por su ID)

        Long buscando;
        if(dir.equals("izq"))
        {
            if(guardado.get().getPosicion()==1) //Se ha intentado mover a la izquierda la primera muestra
                return "redirect:/service/edit?title="+encode(titulo)+"&message=sampleNotEdited";

            buscando = guardado.get().getPosicion()-1; //Hay que mover la muestra una posición a la izquierda, por lo que hay que buscar cual es la muestra con una posición menos
        }
        else
        {
            if(guardado.get().getPosicion()==lista.size()) //Se ha intentado mover a la derecha la última muestra
                return "redirect:/service/edit?title="+encode(titulo)+"&message=sampleNotEdited";

            buscando = guardado.get().getPosicion()+1; //Hay que mover la muestra una posición a la derecha, por lo que hay que buscar cual es la muestra con una posición más
        }

        otro = lista.get((int)(buscando-1)); //La lista está ordenada por posiciones, por lo que simplemente hay que cojer buscando-1, ya que buscando empieza en 1 y la lista en 0

        otro.setPosicion(guardado.get().getPosicion()); //Ponerle la posición de la muestra solicitada a la otra muestra
        guardado.get().setPosicion(buscando); //Ponerle la posición de la muestra que se ha buscado (la otra) a la muestra solicitada


        Optional<Fichero> ficheroEsteOpt = ficheroRepository.findByDireccion(guardado.get().getMultimedia().getDireccion());
        Optional<Fichero> ficheroOtroOpt = ficheroRepository.findByDireccion(otro.getMultimedia().getDireccion());

        if(ficheroEsteOpt.isEmpty() || ficheroOtroOpt.isEmpty())
            return "redirect:/service/edit?title="+encode(titulo)+"&message=fileNotFound";

        Fichero ficheroEste = ficheroEsteOpt.get();
        Fichero ficheroOtro = ficheroOtroOpt.get();

        try
        {
            ficheroEste = ficheroService.cambiarNombreFichero(ficheroEste, "muestra"+guardado.get().getPosicion()+"_");
            ficheroOtro = ficheroService.cambiarNombreFichero(ficheroOtro, "muestra"+otro.getPosicion());
            ficheroRepository.save(ficheroOtro);
            ficheroEste = ficheroService.cambiarNombreFichero(ficheroEste, "muestra"+guardado.get().getPosicion());
            ficheroRepository.save(ficheroEste);
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return "redirect:/service/edit?title="+encode(titulo)+"&message=fileError";
        }

        guardado.get().setMultimedia(ficheroEste);
        otro.setMultimedia(ficheroOtro);

        muestrasRepository.save(guardado.get()); //Guardar las dos muestras para actualizar sus posiciones
        muestrasRepository.save(otro);

        return "redirect:/service/edit?title="+encode(guardado.get().getPadre().getTitulo())+"&message=sampleEdited";
    }

    @PostMapping("rate")
    public String createRating(@ModelAttribute ValorarServicios valoracion, 
                            @RequestParam(value="title", required = true) String titulo)
    {
        if(getUser() == null)
            return "redirect:/error/403";

        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(getUser().getUsername());

        Usuario usuario = usuarioOpt.get(); 

        Optional<Servicio> servicioValorado = servicioRepository.findByTitulo(titulo.trim());
        if(servicioValorado.isEmpty())
            return "redirect:/service/list?message=noServiceFather";

        if(valoracion.getValoracion() > 5)
            return "redirect:/service/view?title="+encode(titulo)+"&message=invalidRating";
        
        for(ValorarServicios i : usuario.getServiciosValorados())
        {
            if(i.getServicio().equals(servicioValorado.get()))
                return "redirect:/service/view?title="+encode(titulo)+"&message=alreadyRated";
        }
        
        valoracion.setServicio(servicioValorado.get());
        valoracion.setUsuario(getUser());
        valoracion.setFecha(new Date());
        valoracion.setComentario(valoracion.getComentario().trim());

        valorarServiciosRepository.save(valoracion);

        // Notificar a través del WebSocket
        //webSocketController.notifyNewRating("New rating created for service: " + titulo);
        webSocketController.notifyNewRating(valoracion);

        return "redirect:/service/view?title="+encode(titulo)+"&message=ratingCreated";
    }

    @GetMapping("deleteRating")
    public String deleteRating(@RequestParam(value="title", required = true) String title, 
                            @RequestParam(value="id", required = true) String id)
    {
        Optional<ValorarServicios> aBorrar;
        if(checkTitle(title) == null)
            return "redirect:/service/list?message=noServiceFather";
            
        try
        {
            aBorrar = valorarServiciosRepository.findById(Long.parseLong(id));
        }
        catch(NumberFormatException e)
        {
            return "redirect:/service/view?title="+encode(title)+"&message=ratingNotDeleted";
        }

        if(aBorrar.isEmpty())
            return "redirect:/service/view?title="+encode(title)+"&message=ratingNotDeleted";

        if(!aBorrar.get().getUsuario().equals(getUser()) && !getUser().isAdmin())
        {
            return "redirect:/error/403";
        }

        valorarServiciosRepository.delete(aBorrar.get());

        // Notificar a través del WebSocket
        webSocketController.notifyDeleteRating(aBorrar.get().getId());

        return "redirect:/service/view?title="+encode(title)+"&message=ratingDeleted";
    }

    @PostMapping("verify")
    public String verify(@RequestParam(value = "title", required = true) String titulo)
    {
        Usuario usuario = getUser();
        Servicio servicio = checkTitle(titulo);

        if(usuario == null || !usuario.isAdmin())
            return "redirect:/error/403";

        if(servicio == null)
            return "redirect:/service/list?message=serviceNotFound";

        servicio.setVerificado(true);
        servicio.setPuntuacion(0L);
        servicioRepository.save(servicio);
        
        return "redirect:/service/view?title="+encode(titulo)+"&message=verified";
    }

    public Servicio guardarServicio(Servicio servicio) 
    {
        if (servicio == null || servicio.getCreador() == null || servicio.getCategoria() == null)
        {
            throw new IllegalArgumentException("notEnoughData");
        }

        Optional<Categoria> categoriaExistente = Optional.ofNullable(categoriaRepository.findByDescripcion(servicio.getCategoria().getDescripcion()));
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
        servicio.setTitulo(servicio.getTitulo().trim());
        servicio.setPortada(null);
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

        padreEncotrado.get().setVerificado(false);
        padreEncotrado.get().setPuntuacion(0L);
        servicioRepository.save(padreEncotrado.get());

        return opcionRepository.save(nuevo);
    }

}