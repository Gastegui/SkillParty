package com.example.securingweb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.securingweb.ORM.Usuario;
import com.example.securingweb.ORM.UsuarioService;

@Controller
public class UserController {

    private InMemoryUserDetailsManager userDetailsManager;
    private UsuarioService us;

    @Autowired
    public UserController(InMemoryUserDetailsManager userDetailsManager, UsuarioService us) {
        this.userDetailsManager = userDetailsManager;
        this.us = us;
    }

    @GetMapping("/login")
    public String cargarLogin(Model modelo)
    {
        modelo.addAttribute("Usuario", new Usuario());
        return "login";
    }

    @PostMapping("/createUser")
    public String createUser(@ModelAttribute Usuario nuevo, Model modelo) 
    {
        System.out.println("createUser");
        
        Usuario user = new Usuario();
        user.setNombre(nuevo.getNombre());
        user.setContraseña(nuevo.getContraseña());

        if(nuevo.getRol() == "")
            user.setRol("USER");
        else
            user.setRol(nuevo.getRol());

        if(us.guardarUsuario(user)==null)
            return "redirect:/login?creado=no";
        
        
        UserDetails newUser = User.withDefaultPasswordEncoder()
        .username(user.getNombre())
        .password(user.getContraseña())
        .roles(user.getRol())
        .build();

        userDetailsManager.createUser(newUser);
        
        return "redirect:/login?creado=sí";
    }

    @GetMapping("/createUser")
    public String createUserGet()
    {
        System.out.println("GET CREATE USER");
        return "redirect:/snake";
    }

    @GetMapping("/snake")
    public String snake(@RequestParam(name="elementos", required = false) String numero,Model modelo)
    {
        if(numero == null)
            modelo.addAttribute("elementos", 15);
        else
        {
            try
            {
                modelo.addAttribute("elementos", Integer.parseInt(numero));
            }
            catch (Exception e)
            {
                modelo.addAttribute("elementos", 15);
            }
        }
        return "snake";
    }

}
