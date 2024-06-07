package com.example.securingweb.Controladores;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.example.securingweb.ORM.cursos.comprarCursos.ComprarCurso;
import com.example.securingweb.ORM.cursos.comprarCursos.ComprarCursoRepository;
import com.example.securingweb.ORM.cursos.curso.Curso;
import com.example.securingweb.ORM.cursos.curso.CursoRepository;
import com.example.securingweb.ORM.cursos.elemento.Elemento;
import com.example.securingweb.ORM.ficheros.FicheroRepository;
import com.example.securingweb.ORM.ficheros.FicheroService;
import com.example.securingweb.ORM.servicios.comprarServicios.ComprarServicio;
import com.example.securingweb.ORM.servicios.comprarServicios.ComprarServiciosRepository;
import com.example.securingweb.ORM.servicios.muestras.Muestra;
import com.example.securingweb.ORM.servicios.servicio.Servicio;
import com.example.securingweb.ORM.servicios.servicio.ServicioRepository;
import com.example.securingweb.ORM.usuarios.usuario.Usuario;
import com.example.securingweb.ORM.usuarios.usuario.UsuarioRepository;
import com.example.securingweb.ORM.usuarios.usuario.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/user")
public class UserController 
{
    private UsuarioService usuarioService;
    private UsuarioRepository usuarioRepository;
    private PasswordEncoder passwordEncoder;
    private ComprarServiciosRepository comprarServiciosRepository;
    private ServicioRepository servicioRepository;
    private FicheroService ficheroService;
    private FicheroRepository ficheroRepository;
    private CursoRepository cursoRepository;
    private ComprarCursoRepository comprarCursoRepository;

    @Autowired
    public UserController(UsuarioService usuarioService,
                        UsuarioRepository usuarioRepository,
                        PasswordEncoder passwordEncoder,
                        ComprarServiciosRepository comprarServiciosRepository,
                        ServicioRepository servicioRepository,
                        FicheroService ficheroService,
                        FicheroRepository ficheroRepository,
                        CursoRepository cursoRepository,
                        ComprarCursoRepository comprarCursoRepository)
        {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.comprarServiciosRepository = comprarServiciosRepository;
        this.servicioRepository = servicioRepository;
        this.ficheroService = ficheroService;
        this.ficheroRepository = ficheroRepository;
        this.cursoRepository = cursoRepository;
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

    private void logout(HttpServletRequest request, HttpServletResponse response)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null)
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        
    }

    private Date getFecha(String fechaStr)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        try
        {
            date = sdf.parse(fechaStr+" 00:00:00");
        }
        catch (Exception e)
        {
            System.out.println("Error parseando la fecha");
            return null;
        }
        long timeInMillis = date.getTime();
        return new Date(timeInMillis);
        
    }

    /* Metodo GET */
    @GetMapping("/create")
    public String cargarLogin(Model modelo)
    {
        modelo.addAttribute("Usuario", new Usuario());
        return "user/createUser"; //pagina
    }

    /* Metodo POST */
    @PostMapping("/create")
    public String createUser(@ModelAttribute Usuario nuevo, @RequestParam(value="creator", required=false) String creador, 
    @RequestParam(value="fecha_nacimiento", required=true) String fechaStr,
    @RequestParam(value="file", required = true) MultipartFile file,
    @RequestParam(value = "aditional", required = false) String telefono,
    Model modelo) 
    {   
        if(nuevo.getUsername().isEmpty())
        {
            return "redirect:/user/Create?noUser";
        }
        if(nuevo.getPassword().isEmpty())
        {
            return "redirect:/user/create?noPass";
        }

        Usuario user = new Usuario();
        user.setUsername(nuevo.getUsername().trim());
        user.setPassword(passwordEncoder.encode(nuevo.getPassword()));
        user.setNombre(nuevo.getNombre().trim());
        user.setApellidos(nuevo.getApellidos().trim());
        user.setEmail(nuevo.getEmail().trim());
        user.setSaldo(BigDecimal.valueOf(50));
        user.setPorCobrar(BigDecimal.valueOf(0));
        user.setPuntuacion((long)0); //TODO: poner la puntuación a los usuarios bien
        
        user.setFechaDeNacimiento(getFecha(fechaStr));
        
        if(creador != null)
        {
            user.setAutoridad("CREATE_ALL"); //TODO: esto debería de depender de lo que se ponga en el formulario
            user.setTelefono(telefono);
        }
        
        user.setAutoridad("USER");
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        

        if((user = usuarioService.guardarUsuario(user)) == null)
        {
            return "redirect:/user/create?message=userExists";
        }
        else
        {
            try
            {
                user.setImagen(ficheroService.crearPerfil(file, user));
                ficheroRepository.save(user.getImagen());
                usuarioRepository.save(user);
            }
            catch(IOException e)
            {
                return "redirect:/user/create?message=fileError";
            }
            return "redirect:/user/login?userCreated";
        }
    }

    @GetMapping("edit")
    public String getEditUser(Model modelo)
    {
        Usuario user = getUser();
        if(user == null)
            return "redirect:/error/403";
        
        modelo.addAttribute("usuario", user);
        
        if(user.isAdmin())
            return "user/editUserAdmin";

        if(user.isCreatorAny())
            return "user/editUserCreator";
        
        return "user/editUser"; //pagina
    }

    @PostMapping("edit")
    public String postEditUser(@ModelAttribute Usuario nuevo, 
        HttpServletRequest request, 
        HttpServletResponse response, 
        @RequestParam(value = "oldPassword") String oldPass,
        @RequestParam(value = "newPassword") String newPass,
        @RequestParam(value = "file") MultipartFile file,
        @RequestParam(value = "fecha", required = true) String fechaStr)
    {
        Optional<Usuario> guardado = usuarioRepository.findById(getUser().getId());

        if(guardado.isEmpty())
            return "redirect:/error/403";

        if(nuevo.getUsername().isBlank())
            return "redirect:/user/edit?message=notEnoughData";
        guardado.get().setUsername(nuevo.getUsername().trim());

        if(newPass != null && !newPass.isBlank() && oldPass != null && !oldPass.isBlank())
        {
            if(!passwordEncoder.matches(oldPass, guardado.get().getPassword()))
                return "redirect:/iser/edit?message=incorrectPassword";
            guardado.get().setPassword(passwordEncoder.encode(newPass));
        }

        if(nuevo.getNombre().isBlank())
            return "redirect:/user/edit?message=notEnoughData";

        guardado.get().setNombre(nuevo.getNombre().trim());

        if(nuevo.getApellidos().isBlank())
            return "redirect:/user/edit?message=notEnoughData";
    
        guardado.get().setApellidos(nuevo.getApellidos().trim());
        guardado.get().setFechaDeNacimiento(getFecha(fechaStr));

        if(guardado.get().isCreatorAny() || guardado.get().isAdmin())
        {
            if(nuevo.getTelefono().isBlank())
                return "redirect:/user/edit?message=notEnoughData";

            guardado.get().setTelefono(nuevo.getTelefono().trim());
        }

        if(nuevo.getEmail().isBlank())
            return "redirect:/user/edit?message=notEnoughData";

        guardado.get().setEmail(nuevo.getEmail().trim());

        if(file != null)
        {
            try
            {
                ficheroRepository.save(ficheroService.cambiarPerfil(file, guardado.get(), guardado.get().getImagen()));
            }
            catch(IOException e)
            {
                return "redirect:/user/edit?message=fileError";
            }
        }

        usuarioRepository.save(guardado.get());
        logout(request, response);

        return "redirect:/user/login"; 
    }


    @GetMapping("delete")
    public String deleteUser(HttpServletRequest request, HttpServletResponse response)
    {
        Usuario actual = getUser();

        if(actual == null)
            return "redirect:/";

        Optional<Usuario> guardado = usuarioRepository.findById(actual.getId());

        if(guardado.isEmpty())
            return "redirect:/?message=userNotFound";

        actual = guardado.get();

        if(actual.isAdmin())
            return "redirect:/error/403";


        try
        {
            ficheroService.borrarFichero(actual.getImagen());
    
            for(Servicio i : actual.getServiciosCreados())
            {
                ficheroService.borrarFichero(i.getPortada());
                for(Muestra j : i.getMuestras())
                    ficheroService.borrarFichero(j.getMultimedia());
            }
    
            for(Curso i : actual.getCursosCreados())
            {
                ficheroService.borrarFichero(i.getPortada());
                for(Elemento j : i.getElementos())
                {
                    if(j.getAdjunto()!= null)
                        ficheroService.borrarFichero(j.getAdjunto());
                    ficheroService.borrarFichero(j.getMultimedia());
                }
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        usuarioRepository.delete(guardado.get());
        logout(request, response);
        return "redirect:/?message=userDeleted";
    }

    @GetMapping("becomePro")
    public String becomePro()  
    {
        Usuario usuario = getUser();

        if(usuario == null || usuario.isPro())
            return "redirect:/error/403";

        return "user/becomePro";
    }

    @GetMapping("panel")
    public String panelUser()
    {
        Usuario usuario = getUser();

        if(usuario == null)
            return "redirect:/error/403";

        if(usuario.isCreatorAny())
            return "user/panelCreator"; //pagina
        
        return "user/panelUser"; //pagina
    }

    @GetMapping("pending")
    public String pending(Model modelo)
    {
        Usuario usuario = getUser();

        if(usuario == null)
            return "redirect:/error/403";
        
        if(!usuario.isCreatorService())
            return "redirect:/error/403";

        modelo.addAttribute("servicios", comprarServiciosRepository.findByTerminadoFalseAndServicioCreadorId(usuario.getId()));

        return "user/pendingServices";
    }

    @GetMapping("bought")
    public String bought(Model modelo)
    {
        Usuario usuario = getUser();

        if(usuario == null)
            return "redirect:/error/403";

        modelo.addAttribute("cursosSinAcabar", comprarCursoRepository.findByTerminadoFalseAndUsuarioId(usuario.getId()));
        modelo.addAttribute("cursosAcabados", comprarCursoRepository.findByTerminadoTrueAndUsuarioId(usuario.getId()));
        modelo.addAttribute("serviciosSinAcabar", comprarServiciosRepository.findByTerminadoFalseAndUsuarioId(usuario.getId()));
        modelo.addAttribute("serviciosAcabados", comprarServiciosRepository.findByTerminadoTrueAndUsuarioId(usuario.getId()));

        return "user/bought";
    }

    @GetMapping("published")
    public String published(Model modelo,
                            @RequestParam(value="id", required = false, defaultValue = "no") String id)
    {
        Usuario usuario;

        if(id.equals("no"))
        {
            usuario = getUser();
            if(usuario == null || !usuario.isCreatorAny())
                return "redirect:/error/403";

        } 
        else
        {
            try
            {
                Optional<Usuario> opt = usuarioRepository.findById(Long.parseLong(id));
                if(opt.isEmpty())
                    throw new NumberFormatException();
                usuario = opt.get();
            }
            catch(NumberFormatException e)
            {
                return "/?message=userNotFound";
            }
        }
        

        modelo.addAttribute("cursos", cursoRepository.findAllByPublicadoTrueAndCreadorId(usuario.getId()));
        modelo.addAttribute("servicios", servicioRepository.findAllByPublicadoTrueAndCreadorId(usuario.getId()));

        return "user/published";
    }

    @GetMapping({"services", "seeServices"})
    public String services(Model modelo, @RequestParam(value = "id", required = false, defaultValue = "") String id)
    {
        Usuario usuario;

        if(id.isBlank())
        {
            usuario = getUser();

            if(usuario == null)
                return "redirect:/error/403";
            
            Optional<Usuario> opt = usuarioRepository.findByUsername(usuario.getUsername());

            if(opt.isEmpty())
                return "redirect:/?message=userNotFound";

            if(!opt.get().isCreatorService())
                return "redirect:/error/403";

            modelo.addAttribute("creador", true);
        }
        else
        {
            try
            {
                Optional<Usuario> opt = usuarioRepository.findById(Long.parseLong(id));
                if(opt.isEmpty())
                    return "redirect:/?message=userNotFound";
                usuario = opt.get();
            }
            catch (NumberFormatException e)
            {
                return "redirect:/?message=userNotFound";
            }

            modelo.addAttribute("creador", false);
        }


        modelo.addAttribute("serviciosNo", servicioRepository.findAllByPublicadoFalseAndCreadorId(usuario.getId()));
        modelo.addAttribute("serviciosSi", servicioRepository.findAllByPublicadoTrueAndCreadorId(usuario.getId()));

        return "user/services";
    }

    @GetMapping({"courses", "seeCourses"})
    public String courses(Model modelo, @RequestParam(value="id", required = false, defaultValue = "") String id)
    {
        Usuario usuario;
        if(id.isBlank())
        {
            usuario = getUser();

            if(usuario == null)
                return "redirect:/error/403";
            
            Optional<Usuario> opt = usuarioRepository.findByUsername(usuario.getUsername());

            if(opt.isEmpty())
                return "redirect:/?message=userNotFound";

            if(!opt.get().isCreatorCourse())
                return "redirect:/error/403";

            modelo.addAttribute("creador", true);
            modelo.addAttribute("cursosNo", cursoRepository.findAllByPublicadoFalseAndCreadorId(usuario.getId()));
        }
        else
        {
            try
            {
                Optional<Usuario> opt = usuarioRepository.findById(Long.parseLong(id));
                if(opt.isEmpty())
                    return "/?message=userNotFound";
                    usuario = opt.get();
            }
            catch (NumberFormatException e)
            {
                return "/?message=userNotFound";
            }
            modelo.addAttribute("creador", false);
        }

        modelo.addAttribute("cursosSi", cursoRepository.findAllByPublicadoTrueAndCreadorId(usuario.getId()));

        return "user/courses";
    }

    @GetMapping("claim")
    public String getClaim(Model modelo, @RequestParam(value="year", required = false) String añoStr)
    {
        Usuario usuario = getUser();

        if(usuario == null || !usuario.isCreatorAny())
            return "redirect:/error/403";

        Optional<Usuario> guardado = usuarioRepository.findByUsername(usuario.getUsername());
        
        if(guardado.isEmpty())    
            return "redirect:/?message=userNotFound";

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

        int año = cal.get(Calendar.YEAR);

        if(añoStr != null)
        {
            try
            {
                año = Integer.parseInt(añoStr);
            }
            catch(NumberFormatException e)
            {
                
            }
        }

        Long lista[] = new Long[12];

        for(int i = 0; i < lista.length; i++)
            lista[i] = 0L;

        for(ComprarCurso i : comprarCursoRepository.findByCursoCreadorId(guardado.get().getId()))
        {
            cal.setTime(i.getFecha());
            if(cal.get(Calendar.YEAR) == año)
                lista[cal.get(Calendar.MONTH)] += i.getPrecio().longValue();
        }
        
        for(ComprarServicio i : comprarServiciosRepository.findByServicioCreadorId(guardado.get().getId()))
        {
            cal.setTime(i.getFecha());
            if(cal.get(Calendar.YEAR) == año)
                lista[cal.get(Calendar.MONTH)] += i.getOpcionCompra().getPrecio().longValue();
        }
                
        modelo.addAttribute("usuario", usuario);
        modelo.addAttribute("beneficios", lista);

        return "user/fondos";
    }

    @PostMapping("claim")
    public String postClaim()
    {
        Usuario usuario = getUser();

        if(usuario == null || !usuario.isCreatorAny())
            return "redirect:/error/403";

        usuario.setSaldo(usuario.getSaldo().add(usuario.getPorCobrar()));
        usuario.setPorCobrar(BigDecimal.ZERO);

        usuarioRepository.save(usuario);

        return "redirect:/user/claim?message=claimed";
    }

    @GetMapping("addBalance")
    public String getAddBalance(Model modelo, @RequestParam(value = "year", required = false) String añoStr)
    {
        Usuario usuario = getUser();

        if(usuario == null)
            return "redirect:/error/403";
            
        Optional<Usuario> guardado = usuarioRepository.findByUsername(usuario.getUsername());
            
        if(guardado.isEmpty())    
            return "redirect:/?message=userNotFound";

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

        int año = cal.get(Calendar.YEAR);

        if(añoStr != null)
        {
            try
            {
                año = Integer.parseInt(añoStr);
            }
            catch(NumberFormatException e)
            {
                
            }
        }

        Long lista[] = new Long[12];

        for(int i = 0; i < lista.length; i++)
            lista[i] = 0L;

        for(ComprarCurso i : guardado.get().getCursosComprados())
        {
            cal.setTime(i.getFecha());
            if(cal.get(Calendar.YEAR) == año)
                lista[cal.get(Calendar.MONTH)] += i.getPrecio().longValue();
        }
        
        for(ComprarServicio i : guardado.get().getServiciosComprados())
        {
            cal.setTime(i.getFecha());
            if(cal.get(Calendar.YEAR) == año)
                lista[cal.get(Calendar.MONTH)] += i.getOpcionCompra().getPrecio().longValue();
        }
               
            
        modelo.addAttribute("usuario", usuario);
        modelo.addAttribute("gastos", lista);

        return "user/addBalance";
    }

    @PostMapping("addBalance")
    public String postAddBalance(@RequestParam(value = "cantidad", required = true) String cantidadStr)
    {
        Usuario usuario = getUser();

        if(usuario == null)
            return "redirect:/error/403";

        try
        {
            usuario.setSaldo(usuario.getSaldo().add(BigDecimal.valueOf(Long.parseLong(cantidadStr))));
            usuarioRepository.save(usuario);
        }
        catch (NumberFormatException e)
        {
            return "redirect:/user/addBalance?message=balanceNotAdded";
        }

        return "redirect:/user/addBalance?message=balanceAdded";
    }

    @GetMapping("/{usuarioId}/comprar-curso/{cursoId}")
    public ResponseEntity<Void> comprarCurso(@PathVariable Long usuarioId, @PathVariable Long cursoId) 
    {
        try 
        {
            usuarioService.descontarSaldoCurso(usuarioId, cursoId);
            return ResponseEntity.ok().build();
        } 
        catch (Exception e) 
        {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{usuarioId}/comprar-servicio/{servicioId}/{opcionId}")
    public ResponseEntity<Void> comprarServicio(@PathVariable Long usuarioId, @PathVariable Long servicioId, @PathVariable Long opcionId) 
    {
        try 
        {
            usuarioService.descontarSaldoServicio(usuarioId, servicioId, opcionId);
            return ResponseEntity.ok().build();
        } 
        catch (Exception e) 
        {
            return ResponseEntity.badRequest().build();
        }
    }
}