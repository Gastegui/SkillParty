package com.example.securingweb.Controladores;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
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
import com.example.securingweb.ORM.cursos.comprarCursos.ComprarCurso;
import com.example.securingweb.ORM.cursos.comprarCursos.ComprarCursoRepository;
import com.example.securingweb.ORM.cursos.curso.Curso;
import com.example.securingweb.ORM.cursos.curso.CursoRepository;
import com.example.securingweb.ORM.cursos.elemento.Elemento;
import com.example.securingweb.ORM.cursos.elemento.ElementoRepository;
import com.example.securingweb.ORM.cursos.tipos.Tipo;
import com.example.securingweb.ORM.cursos.tipos.TipoRepository;
import com.example.securingweb.ORM.cursos.valorarCursos.ValorarCurso;
import com.example.securingweb.ORM.cursos.valorarCursos.ValorarCursoRepository;
import com.example.securingweb.ORM.ficheros.Fichero;
import com.example.securingweb.ORM.ficheros.FicheroRepository;
import com.example.securingweb.ORM.ficheros.FicheroService;
import com.example.securingweb.ORM.idiomas.Idioma;
import com.example.securingweb.ORM.idiomas.IdiomaRepository;
import com.example.securingweb.ORM.usuarios.usuario.Usuario;
import com.example.securingweb.ORM.usuarios.usuario.UsuarioRepository;
import com.example.securingweb.ORM.usuarios.usuario.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/course")
public class CourseController 
{
    private FicheroService ficheroService;
    private FicheroRepository ficheroRepository;
    private CursoRepository cursoRepository;
    private TipoRepository tipoRepository;
    private UsuarioRepository usuarioRepository;
    private ElementoRepository elementoRepository;
    private ValorarCursoRepository valorarCursoRepository;
    private IdiomaRepository idiomaRepository;
    private UsuarioService usuarioService;
    private ComprarCursoRepository comprarCursoRepository;


    @Autowired
    public CourseController(FicheroService ficheroService,
                            FicheroRepository ficheroRepository,
                            CursoRepository cursoRepository,
                            TipoRepository tipoRepository,
                            UsuarioRepository usuarioRepository,
                            ElementoRepository elementoRepository,
                            ValorarCursoRepository valorarCursoRepository,
                            IdiomaRepository idiomaRepository,
                            UsuarioService usuarioService,
                            ComprarCursoRepository comprarCursoRepository)
    {
        this.ficheroService = ficheroService;
        this.ficheroRepository = ficheroRepository;
        this.cursoRepository = cursoRepository;
        this.tipoRepository = tipoRepository;
        this.usuarioRepository = usuarioRepository;
        this.elementoRepository = elementoRepository;
        this.valorarCursoRepository = valorarCursoRepository;
        this.idiomaRepository = idiomaRepository;
        this.usuarioService = usuarioService;
        this.comprarCursoRepository = comprarCursoRepository;
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
     * Devuelve el curso con el nombre <code>title</code> o <code>null</code> si no existe el curso
     * @param title
     * @return <code>Curso</code> con nombre <code>title</code> o <code>null</null>
     */
    private Curso checkTitle(String title) 
    {
        return Optional.of(cursoRepository.findByTitulo(title).get()).orElseGet(() -> null);
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
        return "redirect:/course/list";
    }

    /**
     * Solo carga la pantalla con el formulario de creacion de cursos
     */
    @GetMapping("create")
    public String getCreateCourse(Model modelo)
    {
        Usuario usuario = getUser();
        if(usuario == null || !usuario.isCreatorCourse())
            return "redirect:/error/403";
        
        modelo.addAttribute("Curso", new Curso());
        return "course/createCourse"; //pagina
    }
    
    @GetMapping("list")
    public String courseList(Model modelo,
                            @RequestParam(value = "page", required = false, defaultValue = "0") String page,
                            @RequestParam(value = "size", required = false, defaultValue = "9") String size, 
                            HttpServletRequest request)
    {
        int pageInt = 0; //page indica que página se ha cargado
        int sizeInt = 9; //size indica cuantos cursos se cargan en cada página

        Page<Curso> lista;
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

        if(idiomOptional.isEmpty()) //Si no se ha encontrado el idioma pedido en la DB, cargar todos los cursos
            lista = cursoRepository.findAllByPublicado(true, PageRequest.of(pageInt, sizeInt));
        else
            lista = cursoRepository.findAllByIdioma_idAndPublicado(idiomOptional.get().getId(), true, PageRequest.of(pageInt, sizeInt));
        
        //Cargar todos los atributos necesarios para cargar todo correctamente
        modelo.addAttribute("cursos", lista.getContent());
        modelo.addAttribute("haySiguiente", lista.hasNext());
        modelo.addAttribute("hayAnterior", lista.hasPrevious());
        modelo.addAttribute("pagActual", pageInt);

        return "course/courseList"; //pagina
    }

    @GetMapping("view")
    public String courseView(Model modelo, @RequestParam(value="title", required=false) String titulo)
    {
        Usuario usuario = getUser();
        
        if(titulo == null)
            return "redirect:/";

        Optional<Curso> solicitado = cursoRepository.findByTitulo(titulo.trim()); //.trim() sirve para quitar los espacios en blanco al principio y al final de los strings

        if(solicitado.isEmpty()) //Esto significa que el curso pedido no se ha encontrado en la base de datos
            return "redirect:/error/404"; //404: no encontrado

        if(!solicitado.get().getPublicado()) //Si no está publicado, no mostrarlo. Esto es necesario por si se introduce manualmente en la url el nombre del curso
            return "redirect:/error/404";

        modelo.addAttribute("comprado", false);
        if(usuario != null)
        {
            for(ComprarCurso i : comprarCursoRepository.findByUsuarioUsername(usuario.getUsername()))
            {
                if(i.getUsuario().equals(usuario))
                {
                    modelo.addAttribute("comprado", true);
                    break;
                }
            }
        }
        modelo.addAttribute("curso", solicitado.get());
        modelo.addAttribute("admin", getUser() == null ? false : getUser().isAdmin());
        modelo.addAttribute("usuarioID", getUser() == null ? -1 : getUser().getId());
        modelo.addAttribute("ValorarCursos", new ValorarCurso());
        return "course/courseView"; //pagina
    }

    @PostMapping("create")
    public String postCreateCourse(@ModelAttribute Curso nuevo, 
                                @RequestParam(value="precio", required=true) String precio,
                                @RequestParam(value="tipoParam", required=true) String tipo, 
                                @RequestParam(value="idiomaParam", required=true) String idioma,
                                @RequestParam(value="file", required=true) MultipartFile file) throws IOException
    {
        Curso guardar = new Curso();

        if(getUser() == null || !getUser().isCreatorCourse())
            return "redirect:/error/403";

        guardar.setCreador(getUser());
        guardar.setFechaDeCreacion(new Date());
        guardar.setFechaDeActualizacion(guardar.getFechaDeCreacion());

        Tipo tipoNuevo = new Tipo();
        tipoNuevo.setDescripcion(tipo);
        guardar.setTipo(tipoNuevo);

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
            guardar.setPrecio(BigDecimal.valueOf(Long.parseLong(precio)));
        }
        catch(NumberFormatException e)
        {
            return "redirect:/course/create?message=priceError";
        }

        try 
        {
            if(!ficheroService.isImg(file))
                return "redirect:/course/create?messagee=invalidFile";
            guardar = guardarCurso(guardar); //Guardar el servicio primero sin la portada, ya que todavía no existe el ID del servicio
            guardar.setPortada(ficheroRepository.save(ficheroService.crearFicheroCurso(file, guardar, "portada"))); //Guardar el fichero en el sistema y en la DB
            cursoRepository.save(guardar); //Guardar el servicio con la portada en la DB
        }
        catch (DataIntegrityViolationException e)
        {
            return "redirect:/course/create?message=courseExists";
        }
        catch (IllegalArgumentException e)
        {
            return "redirect:/course/create?message=" + e.getMessage();
        }
        catch (IOException e)
        {
            return "redirect:/course/create?message=fileError";
        }

        return "redirect:/course/edit?title=" + encode(nuevo.getTitulo()) + "&message=courseCreated";
    }

    @GetMapping("delete")
    public String deleteCourse(@RequestParam(value="title", required = true) String titulo)
    {

        Optional<Curso> aBorrar = cursoRepository.findByTitulo(titulo);

        if(aBorrar.isEmpty())
            return "redirect:/user/courses?message=courseNotFound";

        if (!aBorrar.get().getCreador().equals(getUser()) && !getUser().isAdmin())
            return "redirect:/error/403";

        try
        {
            ficheroService.borrarFichero(aBorrar.get().getPortada());
            ficheroRepository.delete(aBorrar.get().getPortada());
            for(Elemento i : aBorrar.get().getElementos())
            {
                ficheroService.borrarFichero(i.getMultimedia());
                ficheroRepository.delete(i.getMultimedia());
                if(i.getAdjunto() != null)
                {
                    ficheroService.borrarFichero(i.getAdjunto());
                    ficheroRepository.delete(i.getAdjunto());
                }
            }
        }
        catch(IOException e)
        {
            return "redirect:/course/edit?title="+encode(titulo)+"&message=fileError";
        }

        cursoRepository.delete(aBorrar.get());

        return "redirect:/user/courses?message=courseDeleted"; //pagina
    }

    @PostMapping("publish")
    public String publishCourse(@RequestParam(value = "private", required = true) String isPrivate,
                                @RequestParam(value = "title" ,required = true) String titulo)
    {
        Optional<Curso> guardado = cursoRepository.findByTitulo(titulo);
        if(guardado.isEmpty())
            return "redirect:/user/courses?message=courseNotFound";

        if(!guardado.get().getCreador().equals(getUser()) && !getUser().isAdmin())
            return "redirect:/error/403";

        guardado.get().setPublicado(isPrivate.equals("y") ? false : true);
        cursoRepository.save(guardado.get());
        return "redirect:/course/edit?title="+encode(guardado.get().getTitulo())+"&message="+(isPrivate.equals("y")? "coursePrivated" : "coursePublished");
    }

    @GetMapping("edit")
    public String updateCourse(Model modelo, @RequestParam(value="title", required = true)String titulo)
    {
        Optional<Curso> aEditar = cursoRepository.findByTitulo(titulo);

        if(aEditar.isEmpty())
            return "redirect:/user/courses?message=courseNotFound";

        if(!aEditar.get().getCreador().equals(getUser()) && !getUser().isAdmin())
            return "redirect:/error/403";

        modelo.addAttribute("Curso", aEditar.get());
        modelo.addAttribute("ListaElementos", aEditar.get().getElementos());
        modelo.addAttribute("Elemento", new Elemento());
        return "course/editCourse"; //pagina
    }

    @PostMapping("editPrice")
    public String postEditCoursePrice(@RequestParam(value="title", required = true) String titulo,
                                    @RequestParam(value = "price", required = true) String precio)
    {
        Optional<Curso> guardado = cursoRepository.findByTitulo(titulo);

        if(guardado.isEmpty())
            return "redirect:/user/courses?message=courseNotFound";
        
        if (!guardado.get().getCreador().equals(getUser()) && !getUser().isAdmin())
            return "redirect:/error/403";
        
        try
        {
            guardado.get().setPrecio(BigDecimal.valueOf(Long.parseLong(precio)));
        }  
        catch (NumberFormatException e)
        {
            return "redirect:/course/edit?title="+encode(titulo)+"&message=courseNotEdited";
        }

        guardado.get().setVerificado(false);
        guardado.get().setPuntuacion(0L);

        cursoRepository.save(guardado.get());

        return "redirect:/course/edit?title="+encode(titulo)+"&message=courseEdited";
    }

    @PostMapping("edit")
    public String postUpdateCourse(@ModelAttribute Curso nuevo, 
                                @RequestParam(value="tituloViejo", required=true) String tituloViejo, 
                                @RequestParam(value="tipoParam", required=true) String tipoParam, 
                                @RequestParam(value="idiomaParam", required=true) String idiomaParam,
                                @RequestParam(value="file", required=false) MultipartFile file) throws IOException 
    {
        Optional<Curso> guardadoOpt = cursoRepository.findByTitulo(tituloViejo);
        

        if (guardadoOpt.isEmpty())
            return "redirect:/user/courses?message=courseNotFound";

        Curso guardado = guardadoOpt.get();

        if (!guardado.getCreador().equals(getUser()) && !getUser().isAdmin())
            return "redirect:/error/403";
        
        if(!tituloViejo.trim().equals(guardado.getTitulo()) && cursoRepository.findByTitulo(nuevo.getTitulo()).isPresent())
            return "redirect:/course/edit?title="+encode(tituloViejo)+"&message=courseExists";

        guardado.setDescripcion(nuevo.getDescripcion());
        guardado.setFechaDeActualizacion(new Date());
        
        if (!guardado.getTipo().getDescripcion().equals(tipoParam)) { //Comprobar si se ha cambiado el tipo
            Optional<Tipo> tipo = Optional.ofNullable(tipoRepository.findByDescripcion(tipoParam));
            if (tipo.isEmpty()) {
                Tipo tipoNuevo = new Tipo();
                tipoNuevo.setDescripcion(tipoParam);
                guardado.setTipo(tipoRepository.save(tipoNuevo));
            } else {
                guardado.setTipo(tipo.get());
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
        
        if(file != null)
        {
            try
            {
                if(!ficheroService.isImg(file))
                    return "redirect:/course/edit?title="+encode(guardado.getTitulo())+"&message=invalidFile";
                ficheroService.cambiarFicheroCurso(file, guardado, "portada", ficheroGuardado.get());
                ficheroRepository.save(ficheroGuardado.get());
            }
            catch(Exception e)
            {
                e.printStackTrace();
                return "redirect:/course/edit?title="+encode(guardado.getTitulo())+"&message=fileError";
            }
        }
        
        guardado.setPortada(ficheroGuardado.get());
        guardado.setVerificado(false);
        guardado.setPuntuacion(0L);
        guardado.setTitulo(nuevo.getTitulo().trim());

        cursoRepository.save(guardado);

        return "redirect:/course/edit?title="+encode(guardado.getTitulo())+"&message=courseEdited";
    }

    @PostMapping("buy")
    public String buyCoUrse(@RequestParam(value="title", required = true) String titulo)
    {
        Usuario usuario = getUser();

        if(usuario == null)
            return "redirect:/error/403";

        Curso curso = checkTitle(titulo);

        if(curso == null)
            return "redirect:/course/list?message=courseNotFound";

        if(usuario.equals(curso.getCreador()))
            return "redirect:/course/view?title="+encode(curso.getTitulo())+"&message=cannotBuyOwnCourse";

        for(ComprarCurso i : comprarCursoRepository.findByUsuarioUsername(usuario.getUsername()))
        {
            if(i.getUsuario().equals(usuario))
                return "redirect:/course/view?title="+encode(curso.getTitulo())+"&message=courseAlreadyBought";
        }
            
        try
        {
            usuarioService.descontarSaldoCurso(usuario.getId(), curso.getId());
            return "redirect:/course/content?title="+encode(titulo)+"&pos=1";
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return "redirect:/course/view?title="+encode(curso.getTitulo())+"&message=noEnoughBalance";
        }
    }

    @PostMapping("createElement")
    public String createElement(@RequestParam(value="title", required=true) String padre, 
                            @RequestParam(value="multimedia", required=true) MultipartFile multimedia, 
                            @RequestParam(value="adjunto", required=false) MultipartFile adjunto, 
                            @RequestParam(value="titulo", required=true) String tituloElemento,
                            @RequestParam(value="texto", required=true) String texto,
                            @RequestParam(value="pos", required=true) String pos)
    {
        Long posLong;
        Curso curso = checkTitle(padre);
        Elemento nuevo = new Elemento();
        if(curso == null)
            return "redirect:/user/courses?message=noCourseFather";

        if(!curso.getCreador().equals(getUser()))
            return "redirect:/error/403";

        try
        {
            posLong = Long.parseLong(pos);
        }
        catch (NumberFormatException e)
        {
            return "redirect:/couse/edit?title="+encode(padre)+"&message=elementNotCreated";
        }

        try
        {
            if(!ficheroService.isImg(multimedia) && !ficheroService.isVid(multimedia))
                return "redirect:/course/edit?title="+encode(padre)+"&message=invalidFile";
            
            nuevo.setMultimedia(ficheroService.crearFicheroCurso(multimedia, curso, "elemento"+pos));
            nuevo.setPosicion(posLong);
            nuevo.setPadre(curso);
            
            if(adjunto != null)
            {
                nuevo.setAdjunto(ficheroService.crearFicheroCurso(adjunto, curso, "adjunto"+pos));
                ficheroRepository.save(nuevo.getAdjunto());
            }
            
            ficheroRepository.save(nuevo.getMultimedia());
            nuevo.setTexto(texto);
            nuevo.setTitulo(tituloElemento);
            elementoRepository.save(nuevo);
            curso.setVerificado(false);
            curso.setPuntuacion(0L);
            cursoRepository.save(curso);
        }
        catch(IOException e)
        {
            return "redirect:/course/edit?title="+encode(padre)+"&message=fileError";
        }
        return "redirect:/course/edit?title="+encode(padre)+"&message=ElementCreated";
    }

    @GetMapping("deleteElement")
    public String deleteElement(@RequestParam(value="title", required = true) String title, 
                            @RequestParam(value="id", required = true) String id)
    {
        Optional<Elemento> aBorrar;

        if(checkTitle(title) == null)
            return "redirect:/user/courses?message=noCourseFather";
            
        try
        {
            aBorrar = elementoRepository.findById(Long.parseLong(id));
        }
        catch(NumberFormatException e)
        {
            return "redirect:/course/edit?title="+encode(title)+"&message=elementNotDeleted";
        }
        
        if(aBorrar.isEmpty())
            return "redirect:/course/edit?title="+encode(title)+"&message=elementNotDeleted";
        
        if(!aBorrar.get().getPadre().getCreador().equals(getUser()) && !getUser().isAdmin())
            return "redirect:/error/403";
        try
        {
            ficheroService.borrarFichero(aBorrar.get().getMultimedia());
            ficheroRepository.delete(aBorrar.get().getMultimedia());
            elementoRepository.delete(aBorrar.get());
            if(aBorrar.get().getAdjunto() != null) 
            {
                ficheroService.borrarFichero(aBorrar.get().getAdjunto());
                ficheroRepository.delete(aBorrar.get().getAdjunto());
            }
        }
        catch (IOException e)
        {
            return "redirect:/course/edit?title="+encode(title)+"&message=fileError";
        }
        
        List<Elemento> lista = checkTitle(title).getElementos(); //Se usa checkTitle para cargar la lista de todas las muesras qua hay despues de borrar la muestra deseada
        if(!lista.isEmpty())
        {
            Long anterior = (long)0;
            try
            {
                for(Elemento i : lista)
                {
                    if(!i.getPosicion().equals(anterior+1)) //Esto comprueba que todas las muestras tengan los números seguidos, ya que al borrar una muestra que no sea la última se rompe la cadena de números
                    {
                        i.setPosicion(anterior+1); //Ponerle el número siguiente al anterior para reestablecer la cadena de números
                        ficheroRepository.save(ficheroService.cambiarNombreFichero(i.getMultimedia(), "muestra"+(anterior+1)));
                        if(i.getAdjunto() != null)
                            ficheroRepository.save(ficheroService.cambiarNombreFichero(i.getAdjunto(), "adjunto"+(anterior+1)));
                        elementoRepository.save(i); //Hay que guardar la muestra de nuevo para actualizarla con su nueva posición
                    }
                    anterior++;
                }
            }
            catch(IOException e)
            {
                e.printStackTrace();
                return "redirect:/course/edit?title="+encode(title)+"&message=fileError";
            }
        }

        return "redirect:/course/edit?title="+encode(title)+"&message=elementDeleted";
    }

    @PostMapping("editElement") //En /editElement NO se cambia la posición del elemento, para eso está /editElementPos
    public String editSample(@RequestParam(value="id", required = true) String id, 
                        @RequestParam(value = "title", required = true) String titulo, 
                        @RequestParam(value = "titulo", required = true) String tituloElemento,
                        @RequestParam(value = "texto", required = true) String texto,
                        @RequestParam(value = "multimedia", required=false) MultipartFile multimediaParam,
                        @RequestParam(value = "adjunto", required=false) MultipartFile adjuntoParam)
    {
        Curso curso = checkTitle(titulo);
        if(curso == null)
            return "redirect:/user/courses?message=noCourseFather";
            
        Optional<Elemento> guardado;

        try
        {
            guardado = elementoRepository.findById(Long.parseLong(id));
        }
        catch(NumberFormatException e)
        {
            return "redirect:/course/view?title="+encode(titulo)+"&message=elementNotEdited";
        }

        if(guardado.isEmpty())
            return "redirect:/course/view?title="+encode(titulo)+"&message=elementNotFound";

        if(!guardado.get().getPadre().getCreador().equals(getUser()) && !getUser().isAdmin())
            return "redirect:/error/403";

        if(multimediaParam != null)
        {
            Optional<Fichero> multimedia = ficheroRepository.findByDireccion(guardado.get().getMultimedia().getDireccion());
            if(multimedia.isEmpty())
                return "redirect:/course/edit?title="+encode(titulo)+"&message=fileNotFound";
            
            try
            {
                if(!ficheroService.isImg(multimediaParam) && !ficheroService.isVid(multimediaParam))
                    return "redirect:/course/edit?title="+encode(titulo)+"&message=invalidFile";
                ficheroService.cambiarFicheroCurso(multimediaParam, curso, "elemento"+guardado.get().getPosicion(), multimedia.get());
                guardado.get().setMultimedia(ficheroRepository.save(multimedia.get()));
            }
            catch(IOException e)
            {
                return "redirect:/course/edit?title="+encode(titulo)+"&message=fileError";
            }
        }

        if(adjuntoParam != null)
        {
            
            try
            {
                if(guardado.get().getAdjunto() == null)
                {
                    Fichero nuevo = new Fichero();
                    nuevo = ficheroService.crearFicheroCurso(adjuntoParam, curso, "adjunto"+guardado.get().getPosicion());
                    guardado.get().setAdjunto(ficheroRepository.save(nuevo));
                }
                else
                {
                    Optional<Fichero> adjunto = ficheroRepository.findByDireccion(guardado.get().getAdjunto().getDireccion());
                    ficheroService.cambiarFicheroCurso(adjuntoParam, curso, "adjunto"+guardado.get().getPosicion(), adjunto.get());
                    guardado.get().setAdjunto(ficheroRepository.save(adjunto.get()));
                }
            }
            catch(IOException e)
            {
                return "redirect:/course/edit?title="+encode(titulo)+"&message=fileError";
            }
        }

        guardado.get().setTitulo(tituloElemento);
        guardado.get().setTexto(texto);

        elementoRepository.save(guardado.get());

        curso.setVerificado(false);
        curso.setPuntuacion(0L);
        cursoRepository.save(curso);

        return "redirect:/course/edit?title="+encode(guardado.get().getPadre().getTitulo())+"&message=sampleEdited";
    }


    @GetMapping("editElementPos")
    public String editElementPos(@RequestParam(value="id", required = true) String id, 
                            @RequestParam(value = "title", required = true) String titulo, 
                            @RequestParam(value = "dir", required = true) String dir)
    {
        Curso curso = checkTitle(titulo);
        if(curso == null)
            return "redirect:/user/courses?message=noCourseFather";
            
        Optional<Elemento> guardado;
        Elemento otro = null;

        if(!curso.getCreador().equals(getUser()) && !getUser().isAdmin())
            return "redirect:/error/403";

        try
        {
            guardado = elementoRepository.findById(Long.parseLong(id));
        }
        catch(NumberFormatException e)
        {
            return "redirect:/course/edit?title="+encode(titulo)+"&message=elementNotEdited";
        }

        if(guardado.isEmpty())
            return "redirect:/course/edit?title="+encode(titulo)+"&message=elementNotFound";

        List<Elemento> lista = curso.getElementos(); //Cargar la lista de muestras (están ordenadas por su posición, no por su ID)

        Long buscando;
        if(dir.equals("izq"))
        {
            if(guardado.get().getPosicion()==1) //Se ha intentado mover a la izquierda la primera muestra
                return "redirect:/course/edit?title="+encode(titulo)+"&message=elementNotEdited";

            buscando = guardado.get().getPosicion()-1; //Hay que mover la muestra una posición a la izquierda, por lo que hay que buscar cual es la muestra con una posición menos
        }
        else
        {
            if(guardado.get().getPosicion()==lista.size()) //Se ha intentado mover a la derecha la última muestra
                return "redirect:/course/edit?title="+encode(titulo)+"&message=elementNotEdited";

            buscando = guardado.get().getPosicion()+1; //Hay que mover la muestra una posición a la derecha, por lo que hay que buscar cual es la muestra con una posición más
        }

        otro = lista.get((int)(buscando-1)); //La lista está ordenada por posiciones, por lo que simplemente hay que cojer buscando-1, ya que buscando empieza en 1 y la lista en 0

        otro.setPosicion(guardado.get().getPosicion()); //Ponerle la posición de la muestra solicitada a la otra muestra
        guardado.get().setPosicion(buscando); //Ponerle la posición de la muestra que se ha buscado (la otra) a la muestra solicitada

        Optional<Fichero> multimediaEsteOpt = ficheroRepository.findByDireccion(guardado.get().getMultimedia().getDireccion());
        Optional<Fichero> multimediaOtroOpt = ficheroRepository.findByDireccion(otro.getMultimedia().getDireccion());
        Optional<Fichero> adjuntoEsteOpt = Optional.empty();
        Optional<Fichero> adjuntoOtroOpt = Optional.empty();

        if(guardado.get().getAdjunto() != null)
            adjuntoEsteOpt = ficheroRepository.findByDireccion(guardado.get().getAdjunto().getDireccion());
        if(otro.getAdjunto() != null)
            adjuntoOtroOpt = ficheroRepository.findByDireccion(otro.getAdjunto().getDireccion());


        if(multimediaEsteOpt.isEmpty() || multimediaOtroOpt.isEmpty())
            return "redirect:/course/edit?title="+encode(titulo)+"&message=fileNotFound";

        Fichero multimediaEste = multimediaEsteOpt.get();
        Fichero multimediaOtro = multimediaOtroOpt.get();
        Fichero adjuntoEste;
        Fichero adjuntoOtro;
        try
        {
            multimediaEste = ficheroService.cambiarNombreFichero(multimediaEste, "elemento"+guardado.get().getPosicion()+"_");
            multimediaOtro = ficheroService.cambiarNombreFichero(multimediaOtro, "elemento"+otro.getPosicion());
            ficheroRepository.save(multimediaOtro);
            multimediaEste = ficheroService.cambiarNombreFichero(multimediaEste, "elemento"+guardado.get().getPosicion());
            ficheroRepository.save(multimediaEste);

            if(adjuntoEsteOpt.isEmpty() && adjuntoOtroOpt.isPresent())
            {
                adjuntoOtro = ficheroService.cambiarNombreFichero(adjuntoOtroOpt.get(), "adjunto"+otro.getPosicion());
                ficheroRepository.save(adjuntoOtro);
                otro.setAdjunto(adjuntoOtro);
            }
            else if (adjuntoEsteOpt.isPresent() && adjuntoOtroOpt.isEmpty())
            {
                adjuntoEste = ficheroService.cambiarNombreFichero(adjuntoEsteOpt.get(), "adjunto"+guardado.get().getPosicion());
                ficheroRepository.save(adjuntoEste);
                guardado.get().setAdjunto(adjuntoEste);
            }
            else if (adjuntoEsteOpt.isPresent() && adjuntoOtroOpt.isPresent())
            {
                adjuntoEste = ficheroService.cambiarNombreFichero(adjuntoEsteOpt.get(), "adjunto"+guardado.get().getPosicion()+"_");
                adjuntoOtro = ficheroService.cambiarNombreFichero(adjuntoOtroOpt.get(), "adjunto"+otro.getPosicion());
                ficheroRepository.save(adjuntoOtro);
                adjuntoEste = ficheroService.cambiarNombreFichero(adjuntoEste, "adjunto"+guardado.get().getPosicion());
                ficheroRepository.save(adjuntoEste);

                guardado.get().setAdjunto(adjuntoEste);
                otro.setAdjunto(adjuntoOtro);
            }

        }
        catch(IOException e)
        {
            e.printStackTrace();
            return "redirect:/course/edit?title="+encode(titulo)+"&message=fileError";
        }

        guardado.get().setMultimedia(multimediaEste);
        otro.setMultimedia(multimediaOtro);

        elementoRepository.save(guardado.get()); //Guardar los dos elementos para actualizar sus posiciones
        elementoRepository.save(otro);

        return "redirect:/course/edit?title="+encode(guardado.get().getPadre().getTitulo())+"&message=sampleEdited";
    }

    @PostMapping("rate")
    public String createRating(@ModelAttribute ValorarCurso valoracion, 
                            @RequestParam(value="title", required = true) String titulo)
    {
        if(getUser() == null)
            return "redirect:/error/403";
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(getUser().getUsername());
        if(usuarioOpt.isEmpty())
            return "redirect:/?message=userNotFound";

        Usuario usuario = usuarioOpt.get();

        Optional<Curso> cursoValorado = cursoRepository.findByTitulo(titulo.trim());
        if(cursoValorado.isEmpty())
            return "redirect:/course/list?message=noCourseFather";

        if(valoracion.getValoracion() > 5)
            return "redirect:/course/view?title="+encode(titulo)+"&message=invalidRating";
        
        for(ValorarCurso i : usuario.getCursosValorados())
        {
            if(i.getCurso().equals(cursoValorado.get()))
                return "redirect:/course/view?title="+encode(titulo)+"&message=alreadyRated";
        }
        
        valoracion.setCurso(cursoValorado.get());
        valoracion.setUsuario(getUser());
        valoracion.setFecha(new Date());
        valoracion.setComentario(valoracion.getComentario().trim());

        valorarCursoRepository.save(valoracion);

        return "redirect:/course/view?title="+encode(titulo)+"&message=ratingCreated";
    }

    @GetMapping("deleteRating")
    public String deleteRating(@RequestParam(value="title", required = true) String title, 
                            @RequestParam(value="id", required = true) String id)
    {
        Optional<ValorarCurso> aBorrar;
        if(checkTitle(title) == null)
            return "redirect:/course/list?message=noCourseFather"; 
            
        try
        {
            aBorrar = valorarCursoRepository.findById(Long.parseLong(id));
        }
        catch(NumberFormatException e)
        {
            return "redirect:/course/view?title="+encode(title)+"&message=ratingNotDeleted";
        }

        if(aBorrar.isEmpty())
            return "redirect:/course/view?title="+encode(title)+"&message=ratingNotDeleted";
        
        if(!aBorrar.get().getUsuario().equals(getUser()) && !getUser().isAdmin())
        {
            return "redirect:/error/403";
        }

        valorarCursoRepository.delete(aBorrar.get());

        return "redirect:/course/view?title="+encode(title)+"&message=ratingDeleted";
    }

    @PostMapping("verify")
    public String verify(@RequestParam(value = "title", required = true) String titulo,
                        @RequestParam(value = "puntuacion", required = true) String puntuacion)
    {
        Usuario usuario = getUser();
        Curso curso = checkTitle(titulo);

        if(usuario == null || !usuario.isAdmin())
            return "redirect:/error/403";

        if(curso == null)
            return "redirect:/course/list?message=courseNotFound";

        curso.setVerificado(true);
        try
        {
            curso.setPuntuacion(Long.parseLong(puntuacion));
        }
        catch(NumberFormatException e)
        {
            return "redirect:/course/view?title="+encode(titulo)+"&message=notVerified";
        }

        cursoRepository.save(curso);
        
        return "redirect:/course/view?title="+encode(titulo)+"&message=verified";
    }
    
    @GetMapping("content")
    public String content(Model modelo,
                        @RequestParam(value="title", required = true) String titulo,
                        @RequestParam(value="pos", required = true) String pos)
    {
        if(getUser() == null)
            return "redirect:/error/403";

        Optional<Usuario> opt = usuarioRepository.findByUsername(getUser().getUsername());
        if(opt.isEmpty())
            return "redirect:/?message=userNotFound";

        Usuario usuario = opt.get();

        Optional<Curso> curso = cursoRepository.findByTitulo(titulo);

        boolean comprado = false;
        boolean acabado = false;

        if(!usuario.isAdmin())
        {
            for(ComprarCurso i : usuario.getCursosComprados())
            {
                if(i.getCurso().equals(curso.get()))
                    {
                        comprado = true;
                        acabado = i.getTerminado();
                        break;
                    }
            }
        }
        else
            comprado = true;

        if(!comprado)
            return "redirect:/error/403";

        try
        {
            int posicion = Integer.parseInt(pos) - 1;
            modelo.addAttribute("elemento", curso.get().getElementos().get(posicion));
            modelo.addAttribute("tamaño", curso.get().getElementos().size());
            modelo.addAttribute("acabado", acabado);
        }
        catch(NumberFormatException | IndexOutOfBoundsException e)
        {
            return "redirect:/course/view?title="+encode(titulo)+"&message=coursePosError";
        }
        
        return "course/content";
    }

    @GetMapping("finish")
    public String finish(@RequestParam(value="title", required = true) String titulo)
    {
        Usuario usuario = getUser();

        if(usuario == null)
            return "redirect:/error/403";
        
        if(checkTitle(titulo) == null)
            return "redirect:/?message=courseNotFound";

        Optional<ComprarCurso> compra = comprarCursoRepository.findByUsuarioUsernameAndCursoTitulo(usuario.getUsername(), titulo);
        if(compra.isEmpty())
            return "redirect:/course/view?title"+encode(titulo)+"&message=courseNotBought";

        compra.get().setTerminado(true);

        comprarCursoRepository.save(compra.get());

        return "user/bought?message=courseFinished";
    }


    public Curso guardarCurso(Curso curso) 
    {
        if (curso == null || curso.getCreador() == null || curso.getTipo() == null)
        {
            throw new IllegalArgumentException("notEnoughData");
        }

        Optional<Tipo> tipoExistente = Optional.ofNullable(tipoRepository.findByDescripcion(curso.getTipo().getDescripcion()));
        Tipo tipo;
        if (tipoExistente.isPresent()) {
            tipo = tipoExistente.get();
        } else {
            tipo = tipoRepository.save(curso.getTipo());
        }
    
        Optional<Usuario> creadorExistente = usuarioRepository.findById(curso.getCreador().getId());
        if (creadorExistente.isEmpty()) {
            throw new IllegalArgumentException("noCourseCreator");
        }
    
        curso.setCreador(creadorExistente.get());
        curso.setTipo(tipo);
        curso.setTitulo(curso.getTitulo().trim());
        curso.setPortada(null);
        return cursoRepository.save(curso);
    }
    
}
