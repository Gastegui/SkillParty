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
import com.example.securingweb.ORM.ficheros.Fichero;
import com.example.securingweb.ORM.ficheros.FicheroRepository;
import com.example.securingweb.ORM.idiomas.Idioma;
import com.example.securingweb.ORM.idiomas.IdiomaRepository;
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
    private IdiomaRepository idiomaRepository;


    @Autowired
    public ServiceController(ServicioRepository servicioRepository, CategoriaRepository categoriaRepository, FicheroRepository ficheroRepository, UsuarioRepository usuarioRepository, OpcionRepository opcionRepository, MuestrasRepository muestrasRepository, ValorarServiciosRepository valorarServiciosRepository, IdiomaRepository idiomaRepository)
    {
        this.servicioRepository = servicioRepository;
        this.categoriaRepository = categoriaRepository;
        this.ficheroRepository = ficheroRepository;
        this.usuarioRepository = usuarioRepository;
        this.opcionRepository = opcionRepository;
        this.muestrasRepository = muestrasRepository;
        this.valorarServiciosRepository = valorarServiciosRepository;
        this.idiomaRepository = idiomaRepository;
    }

    /**
     * 
     * @return el usuario loggeado
     */
    private Usuario getUser()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Usuario) authentication.getPrincipal();
    }

    /**
     * Devuelve el servicio con el nombre <code>title</code> o <code>null</code> si no existe el servicio
     * @param title
     * @return <code>Servicio</code> con nombre <code>title</code> o <code>null</null>
     */
    public Servicio checkTitle(String title) 
    {
        return Optional.of(servicioRepository.findByTitulo(title).get()).orElseGet(() -> null);
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
        return "createService";
    }
    
    @GetMapping("list")
    public String serviceList(Model modelo, 
                            @RequestParam(value="page", required=false, defaultValue = "0") String page, 
                            @RequestParam(value="size", required=false, defaultValue = "9") String size, 
                            @RequestParam(value="lang", required=false, defaultValue = "es") String idioma)
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

        Optional<Idioma> idiomOptional = idiomaRepository.findByIdioma(idioma);

        if(idiomOptional.isEmpty()) //Si no se ha encontrado el idioma pedido en la DB, cargar todos los servicios
            lista = servicioRepository.findAllByPublicado(true, PageRequest.of(pageInt, sizeInt));
        else
            lista = servicioRepository.findAllByIdioma_idAndPublicado(idiomOptional.get().getId(), true, PageRequest.of(pageInt, sizeInt));
        
        //Cargar todos los atributos necesarios para cargar todo correctamente
        modelo.addAttribute("servicios", lista.getContent());
        modelo.addAttribute("haySiguiente", lista.hasNext());
        modelo.addAttribute("hayAnterior", lista.hasPrevious());
        modelo.addAttribute("pagActual", pageInt);

        return "serviceList";
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
        modelo.addAttribute("ValorarServicios", new ValorarServicios());
        return "serviceView";
    }

    @PostMapping("create")
    public String postCreateService(@ModelAttribute Servicio nuevo, 
                                @RequestParam(value="categoriaParam", required=true) String categoria, 
                                @RequestParam(value="portadaDireccion", required=true) String direccion, 
                                @RequestParam(value="idiomaParam", required=true) String idioma)
    {
        Servicio guardar = new Servicio();

        guardar.setCreador(getUser());

        guardar.setFechaDeCreacion(new Date());

        Categoria catNueva = new Categoria();
        catNueva.setDescripcion(categoria);

        guardar.setCategoria(catNueva);

        guardar.setDescripcion(nuevo.getDescripcion().trim());

        guardar.setPublicado(false);

        Optional<Idioma> idiomaEncontrado = idiomaRepository.findByIdioma(idioma);
        if(idiomaEncontrado.isEmpty())
            return "redirect:/error";

        guardar.setIdioma(idiomaEncontrado.get());

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
            return "redirect:/service/create?message=serviceExists";
        }
        catch(IllegalArgumentException e)
        {
            return "redirect:/service/create?message="+e.getMessage();
        }

        return "redirect:/service/edit?title="+nuevo.getTitulo()+"&message=serviceCreated";
    }

    @GetMapping("delete")
    public String deleteService(@RequestParam(value="title", required = true) String titulo)
    {

        Optional<Servicio> aBorrar = servicioRepository.findByTitulo(titulo);

        if(aBorrar.isEmpty())
            return "redirect:/service/list?message=serviceNotFound"; //MARK: esto debería redirigir a la lista de servicios creados por el usuario

        if (!aBorrar.get().getCreador().equals(getUser()) && !getUser().isAdmin())
            return "redirect:/error/403";

        servicioRepository.delete(aBorrar.get());

        return "redirect:/service/list?message=serviceDeleted"; //MARK: esto debería redirigir a la lista de servicios creados por el usuario
    }

    @PostMapping("publish")
    public String publishService(@ModelAttribute Servicio aPublicar, @RequestParam(value = "private", required = true) String isPrivate)
    {
        Optional<Servicio> guardado = servicioRepository.findByTitulo(aPublicar.getTitulo());
        if(guardado.isEmpty())
            return "redirect:/service/list?message=serviceNotFound"; //MARK: esto debería redirigir a la lista de servicios creados por el usuario

        if(!guardado.get().getCreador().equals(getUser()) && !getUser().isAdmin())
            return "redirect:/error/403";

        guardado.get().setPublicado(isPrivate.equals("y") ? false : true);
        servicioRepository.save(guardado.get());
        return "redirect:/service/edit?title="+guardado.get().getTitulo()+"&message="+(isPrivate.equals("y")? "servicePrivated" : "servicePublished");
    }

    @GetMapping("edit")
    public String updateService(Model modelo, @RequestParam(value="title", required = true)String titulo)
    {
        Optional<Servicio> aEditar = servicioRepository.findByTitulo(titulo);

        if(aEditar.isEmpty())
            return "redirect:/service/list?message=serviceNotFound"; //MARK: esto debería redirigir a la lista de servicios creados por el usuario

        if(!aEditar.get().getCreador().equals(getUser()) && !getUser().isAdmin())
            return "redirect:/error/403";

        modelo.addAttribute("Servicio", aEditar.get());
        modelo.addAttribute("ListaOpciones", aEditar.get().getOpciones());
        modelo.addAttribute("ListaMuestras", aEditar.get().getMuestras());
        modelo.addAttribute("Opcion", new Opcion());
        modelo.addAttribute("Muestra", new Muestra());
        return "editService";
    }

    @PostMapping("edit")
    public String PostUpdateService(@ModelAttribute Servicio nuevo, 
                                @RequestParam(value="tituloViejo", required=true) String tituloViejo, 
                                @RequestParam(value="categoriaParam", required=true) String categoriaParam, 
                                @RequestParam(value="idiomaParam", required=true) String idiomaParam)
    {
        Optional<Servicio> guardado = servicioRepository.findByTitulo(tituloViejo);

        if(guardado.isEmpty())
            return "redirect:/service/list?message=serviceNotFound"; //MARK: esto debería redirigir a la lista de servicios creados por el usuario

        if(!guardado.get().getCreador().equals(getUser()) && !getUser().isAdmin())
            return "redirect:/error/403";

        guardado.get().setTitulo(nuevo.getTitulo());
        guardado.get().setDescripcion(nuevo.getDescripcion());
        guardado.get().setFechaDeActualizacion(new Date());
        
        if(!guardado.get().getCategoria().getDescripcion().equals(categoriaParam)) //Comprobar si se ha cambiado la categoría
        {
            Optional<Categoria> cat = categoriaRepository.findByDescripcion(categoriaParam);
            if(cat.isEmpty())
            {
                Categoria catNueva = new Categoria();
                catNueva.setDescripcion(categoriaParam);
                guardado.get().setCategoria(categoriaRepository.save(catNueva));
            }
            else
                guardado.get().setCategoria(cat.get());
        }
        //TODO: ARCHIVO
        //Actualizar el archivo si el creador ha subido uno nuevo

        if(!guardado.get().getIdioma().getIdioma().equals(idiomaParam)) //Comprobar si se ha cambiado el idioma
        {
            Optional<Idioma> idi = idiomaRepository.findByIdioma(idiomaParam);
            if(idi.isEmpty())
            {
                Idioma idiNuevo = new Idioma();
                idiNuevo.setIdioma(idiomaParam);
                guardado.get().setIdioma(idiomaRepository.save(idiNuevo));
            }
            else
                guardado.get().setIdioma(idi.get());
        }

        servicioRepository.save(guardado.get());

        return "redirect:/service/edit?title="+guardado.get().getTitulo()+"&message=serviceEdited";
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
            return "redirect:/service/create?message="+e.getMessage(); //MARK: esto debería redirigir a la lista de servicios creados por el usuario
        }
        catch(NoPermissionException e)
        {
            return "redirect:/error/403";
        }

        return "redirect:/service/edit?title="+padre+"&message=optionCreated";
    }
    
    @GetMapping("deleteOption")
    public String deleteOption(@RequestParam(value="title", required = true) String title,
                            @RequestParam(value="id", required = true) String id)
    {
        Optional<Opcion> aBorrar;
        if(checkTitle(title) == null)
            return "redirect:/service/list?message=noServiceFather"; //MARK: esto debería redirigir a la lista de servicios creados por el usuario

        try
        {
            aBorrar = opcionRepository.findById(Long.parseLong(id));
        }
        catch (NumberFormatException e)
        {
            return "redirect:/service/edit?title="+title+"&message=optionNotDeleted";
        }

        if(aBorrar.isEmpty())
            return "redirect:/service/edit?title="+title+"&message=optionNotFound";

        if(!aBorrar.get().getPadre().getCreador().equals(getUser()) && !getUser().isAdmin())
            return "redirect:/error/403";

        opcionRepository.delete(aBorrar.get());

        return "redirect:/service/edit?title="+title+"&message=optionDeleted";
    }

    @PostMapping("editOption")
    public String updateOption(@RequestParam(value = "id", required = true) String id, 
                            @RequestParam(value="title", required = true) String title, 
                            @RequestParam(value = "desc", required = true) String desc, 
                            @RequestParam(value = "precio", required = true) String precio)
    {
        if(checkTitle(title) == null)
            return "redirect:/service/list?message=noServiceFather"; //MARK: esto debería redirigir a la lista de servicios creados por el usuario
            
        Optional<Opcion> guardado;
        try
        {
            guardado= opcionRepository.findById(Long.parseLong(id));
        }
        catch (NumberFormatException e)
        {
            return "redirect:/service/edit?title="+title+"&message=optionNotEdited";
        }

        if(guardado.isEmpty())
            return "redirect:/service/edit?title="+title+"&message=optionNotFound";

        if(!guardado.get().getPadre().getCreador().equals(getUser()) && !getUser().isAdmin())
            return "redirect:/error/403";

        guardado.get().setPrecio(new BigDecimal(precio));
        guardado.get().setDescripcion(desc.trim());

        try
        {
            guardarOpcion(guardado.get(), title);
        }
        catch(IllegalArgumentException e)
        {
            return "redirect:/service/create?message="+e.getMessage(); //MARK: esto debería redirigir a la lista de servicios creados por el usuario
        }
        catch(NoPermissionException e)
        {
            return "redirect:/error/403";
        }

        return "redirect:/service/edit?title="+title+"&message=optionEdited";
    }

    @PostMapping("createSample")
    public String createSample(@ModelAttribute Muestra nuevo, 
                        @RequestParam(value="title", required=true) String padre, 
                        @RequestParam(value="dirMultimedia", required=true) String dirMultimedia, 
                        @RequestParam(value="pos", required=true) String pos)
    {
        Long posLong;

        if(checkTitle(padre) == null)
            return "redirect:/service/list?message=noServiceFather"; //MARK: esto debería redirigir a la lista de servicios creados por el usuario
            
        try
        {
            posLong = Long.parseLong(pos);
        }
        catch (NumberFormatException e)
        {
            return "redirect:/service/edit?title="+padre+"&message=sampleNotCreated";
        }

        try
        {
            Fichero multimedia = new Fichero();
            multimedia.setDireccion(dirMultimedia.trim());
            nuevo.setFichero(multimedia);
            nuevo.setDescripcion(nuevo.getDescripcion().trim());
            nuevo.setPosicion(posLong);
            guardarMuestra(nuevo, padre);
        }
        catch(IllegalArgumentException e) //noServiceFather
        {
            return "redirect:/service/create?message="+e.getMessage();//MARK: esto debería redirigir a la lista de servicios creados por el usuario
        }
        catch(NoPermissionException e)
        {
            return "redirect:/error/403";
        }
        return "redirect:/service/edit?title="+padre+"&message=sampleCreated";
    }

    @GetMapping("deleteSample")
    public String deleteSample(@RequestParam(value="title", required = true) String title, 
                            @RequestParam(value="id", required = true) String id)
    {
        Optional<Muestra> aBorrar;

        if(checkTitle(title) == null)
            return "redirect:/service/list?message=noServiceFather"; //MARK: esto debería redirigir a la lista de servicios creados por el usuario
            
        try
        {
            aBorrar = muestrasRepository.findById(Long.parseLong(id));
        }
        catch(NumberFormatException e)
        {
            return "redirect:/service/edit?title="+title+"&message=sampleNotDeleted";
        }
        
        if(aBorrar.isEmpty())
            return "redirect:/service/edit?title="+title+"&message=sampleNotDeleted";
        
        if(!aBorrar.get().getPadre().getCreador().equals(getUser()) && !getUser().isAdmin())
            return "redirect:/error/403";
        
        muestrasRepository.delete(aBorrar.get());
        
        List<Muestra> lista = checkTitle(title).getMuestras(); //Se usa checkTitle para cargar la lista de todas las muesras qua hay despues de borrar la muestra deseada

        Long anterior = lista.get(0).getPosicion()-1; //Esto siempre debería ser 0, pero por si acaso le pongo el valor de la primera muestra menos uno para que el for sea más sencillo
        for(Muestra i : lista)
        {
            if(i.getPosicion() != anterior+1) //Esto comprueba que todas las muestras tengan los números seguidos, ya que al borrar una muestra que no sea la última se rompe la cadena de números
            {
                i.setPosicion(anterior+1); //Ponerle el número siguiente al anterior para reestablecer la cadena de números
                muestrasRepository.save(i); //Hay que guardar la muestra de nuevo para actualizarla con su nueva posición
            }
            anterior++;
        }

        return "redirect:/service/edit?title="+title+"&message=sampleDeleted";
    }

    @PostMapping("editSample") //En /editSample NO se cambia la posición de la muestra, para eso está /editSamplePos
    public String editSample(@RequestParam(value="id", required = true) String id, 
                        @RequestParam(value = "title", required = true) String titulo, 
                        @RequestParam(value = "desc", required = true) String desc, 
                        @RequestParam(value="dirMultimedia", required = true) String dirMultimedia)
    {
        if(checkTitle(titulo) == null)
            return "redirect:/service/list?message=noServiceFather"; //MARK: esto debería redirigir a la lista de servicios creados por el usuario
            
        Optional<Muestra> guardado;

        try
        {
            guardado = muestrasRepository.findById(Long.parseLong(id));
        }
        catch(NumberFormatException e)
        {
            return "redirect:/service/view?title="+titulo+"&message=sampleNotEdited";
        }

        if(guardado.isEmpty())
            return "redirect:/service/view?title="+titulo+"&message=sampleNotFound";

        if(!guardado.get().getPadre().getCreador().equals(getUser()) && !getUser().isAdmin())
            return "redirect:/error/403";

        guardado.get().setDescripcion(desc);

        //TODO: ARCHIVO

        muestrasRepository.save(guardado.get());

        return "redirect:/service/edit?title="+guardado.get().getPadre().getTitulo()+"&message=sampleEdited";
    }

    @GetMapping("editSamplePos")
    public String editSamplePos(@RequestParam(value="id", required = true) String id, 
    @RequestParam(value = "title", required = true) String titulo, 
    @RequestParam(value = "dir", required = true) String dir)
    {
        if(checkTitle(titulo) == null)
            return "redirect:/service/list?message=noServiceFather"; //MARK: esto debería redirigir a la lista de servicios creados por el usuario
            
        Optional<Muestra> guardado;
        Muestra otro = null;

        try
        {
            guardado = muestrasRepository.findById(Long.parseLong(id));
        }
        catch(NumberFormatException e)
        {
            return "redirect:/service/view?title="+titulo+"&message=sampleNotEdited";
        }

        if(guardado.isEmpty())
            return "redirect:/service/view?title="+titulo+"&message=sampleNotFound";

        if(!guardado.get().getPadre().getCreador().equals(getUser()) && !getUser().isAdmin())
            return "redirect:/error/403";


        List<Muestra> lista = checkTitle(titulo).getMuestras(); //Cargar la lista de muestras (están ordenadas por su posición, no por su ID)

        Long buscando;
        if(dir.equals("izq"))
        {
            if(guardado.get().getPosicion()==1) //Se ha intentado mover a la izquierda la primera muestra
                return "redirect:/service/edit?title="+titulo+"&sampleNotEdited";

            buscando = guardado.get().getPosicion()-1; //Hay que mover la muestra una posición a la izquierda, por lo que hay que buscar cual es la muestra con una posición menos
        }
        else
        {
            if(guardado.get().getPosicion()==lista.size()) //Se ha intentado mover a la derecha la última muestra
                return "redirect:/service/edit?title="+titulo+"&sampleNotEdited";

            buscando = guardado.get().getPosicion()+1; //Hay que mover la muestra una posición a la derecha, por lo que hay que buscar cual es la muestra con una posición más
        }

        otro = lista.get((int)(buscando-1)); //La lista está ordenada por posiciones, por lo que simplemente hay que cojer buscando-1, ya que buscando empieza en 1 y la lista en 0

        otro.setPosicion(guardado.get().getPosicion()); //Ponerle la posición de la muestra solicitada a la otra muestra
        guardado.get().setPosicion(buscando); //Ponerle la posición de la muestra que se ha buscado (la otra) a la muestra solicitada

        muestrasRepository.save(guardado.get()); //Guardar las dos muestras para actualizar sus posiciones
        muestrasRepository.save(otro);

        return "redirect:/service/edit?title="+guardado.get().getPadre().getTitulo()+"&message=sampleEdited";
    }

    @PostMapping("rate")
    public String createRating(@ModelAttribute ValorarServicios valoracion, 
                            @RequestParam(value="title", required = true) String titulo)
    {

        Optional<Servicio> servicioValorado = servicioRepository.findByTitulo(titulo.trim());
        if(servicioValorado.isEmpty())
            return "redirect:/service/list?message=noServiceFather"; //MARK: esto debería redirigir a la lista de servicios creados por el usuario

        valoracion.setServicio(servicioValorado.get());
        valoracion.setUsuario(getUser());
        valoracion.setFecha(new Date());
        valoracion.setComentario(valoracion.getComentario().trim());

        valorarServiciosRepository.save(valoracion);

        return "redirect:/service/view?title="+titulo;
    }

    @GetMapping("deleteRating")
    public String deleteRating(@RequestParam(value="title", required = true) String title, 
                            @RequestParam(value="id", required = true) String id)
    {
        Optional<ValorarServicios> aBorrar;
        if(checkTitle(title) == null)
            return "redirect:/service/list?message=noServiceFather"; //MARK: esto debería redirigir a la lista de servicios creados por el usuario
            
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

        if(!aBorrar.get().getUsuario().equals(getUser()) && !getUser().isAdmin())
        {
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

        Optional<Fichero> ficheroExistente = ficheroRepository.findByDireccion(servicio.getPortada().getDireccion()); //Buscar el fichero que se quiere guardar entre los ya existentes
        Fichero portada;
        if (ficheroExistente.isEmpty())
            portada = ficheroRepository.save(servicio.getPortada()); //Si no existe en la DB, se guarda el fichero en la DB y se guarda la referencia al archivo guardado
        else
            portada = ficheroExistente.get(); //Si ya en la DB, se guarda su referencia
    
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
        servicio.setPortada(portada); //Se asigna la referencia a la DB para que las relaciones padre a hijo se hagan bien de forma automática con JPA
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