package com.example.securingweb.Controladores;

import java.sql.Date;
import java.text.SimpleDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.securingweb.ORM.usuario.Usuario;
import com.example.securingweb.ORM.usuario.UsuarioService;

/* Controlador para manejar las solicitudes relacionadas con la creación de usuarios */
@Controller
public class LoginController
{
    private UsuarioService us;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public LoginController(UsuarioService us, PasswordEncoder passwordEncoder) 
    {
        this.us = us;
        this.passwordEncoder = passwordEncoder;
    }

    /* Metodo GET */
    @GetMapping("/createUser")
    public String cargarLogin(Model modelo)
    {
        modelo.addAttribute("Usuario", new Usuario());
        return "createUser";
    }

    /* Metodo POST */
    @PostMapping("/createUser")
    public String createUser(@ModelAttribute Usuario nuevo, @RequestParam(value="creator", required=false) String creador, @RequestParam(value="fecha_nacimiento", required=true) String fechaStr, Model modelo) 
    {   
        if(nuevo.getUsername().isEmpty())
        {
            return "redirect:/createUser?noUser";
        }
        if(nuevo.getPassword().isEmpty())
        {
            return "redirect:/createUser?noPass";
        }

        Usuario user = new Usuario();
        user.setUsername(nuevo.getUsername().trim());
        user.setPassword(passwordEncoder.encode(nuevo.getPassword()));
        user.setNombre(nuevo.getNombre().trim());
        user.setApellidos(nuevo.getApellidos().trim());
        user.setEmail(nuevo.getEmail().trim());
        
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
        
        if(us.guardarUsuario(user)==null)
        {
            return "redirect:/createUser?exists";
        }
        else
        {
            return "redirect:/login?created";
        }
    }
}