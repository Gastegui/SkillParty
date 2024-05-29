package com.example.securingweb.Controladores;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;
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

    @Autowired
    public UserController(UsuarioService usuarioService, UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder)
    {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
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

    @GetMapping("addBalance") //MARK: esto no tiene el más mínimo sentido, pero como no vamos a usar ni tarjetas de crédito ni otros como paypal,
    public String addBalance()  //utilizamos un sistema de saldo para pagar las cosas.
    {
        Usuario user = getUser();

        if(user == null)
            return "redirect:/error/403";
        
        user.setSaldo(user.getSaldo().add(BigDecimal.TEN));
        usuarioRepository.save(user);
        return "redirect:/?message=balanceAdded";
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
    public String createUser(@ModelAttribute Usuario nuevo, @RequestParam(value="creator", required=false) String creador, @RequestParam(value="fecha_nacimiento", required=true) String fechaStr, Model modelo) 
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
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date date;
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
        Date fecha = new Date(timeInMillis);
        
        user.setFechaDeNacimiento(fecha);
        
        if(creador != null)
        {
            user.setAutoridad("CREATE_SERVICE");
            user.setTelefono(nuevo.getTelefono());
        }
        
        user.setAutoridad("USER");
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        
        if(usuarioService.guardarUsuario(user) == null)
        {
            return "redirect:/user/create?exists";
        }
        else
        {
            return "redirect:/user/login?created";
        }
    }

    @GetMapping("edit")
    public String getEditUser(Model modelo)
    {
        Usuario user = getUser();
        if(user == null)
            return "redirect:/error/403";
        
        modelo.addAttribute("usuario", user);
        
        if(user.isCreatorAny())
            return "editUserCreator";
        
        return "user/editUser"; //pagina
    }

    @PostMapping("edit")
    public String postEditUser(@ModelAttribute Usuario nuevo, 
        HttpServletRequest request, 
        HttpServletResponse response, 
        @RequestParam(value = "oldPassword") String oldPass,
        @RequestParam(value = "newPassword") String newPass)
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

        if(!guardado.get().getFechaDeNacimiento().equals(nuevo.getFechaDeNacimiento()))
            guardado.get().setFechaDeNacimiento(nuevo.getFechaDeNacimiento());

        if(guardado.get().isCreatorAny() || guardado.get().isAdmin())
        {
            if(nuevo.getTelefono().isBlank())
                return "redirect:/user/edit?message=notEnoughData";

            guardado.get().setTelefono(nuevo.getTelefono().trim());
        }

        if(nuevo.getEmail().isBlank())
            return "redirect:/user/edit?message=notEnoughData";

        guardado.get().setEmail(nuevo.getEmail().trim());

        //MARK: FOTO DE PERFIL

        usuarioRepository.save(guardado.get());
        logout(request, response);

        return "redirect:/user/login"; 
    }


    @PostMapping("delete")
    public String deleteUser(HttpServletRequest request, HttpServletResponse response)
    {
        Usuario actual = getUser();

        if(actual == null)
            return "redirect:/";

        Optional<Usuario> guardado = usuarioRepository.findById(actual.getId());

        if(guardado.isEmpty())
            return "redirect:/=message=userNotFound";

        usuarioRepository.delete(guardado.get());
        logout(request, response);
        return "redirect:/";
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