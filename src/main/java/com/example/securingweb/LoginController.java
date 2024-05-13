package com.example.securingweb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.securingweb.ORM.Usuario;
import com.example.securingweb.ORM.UsuarioService;

@Controller
public class LoginController {

    private UsuarioService us;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public LoginController(UsuarioService us, PasswordEncoder passwordEncoder) {
        this.us = us;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String cargarLogin(Model modelo)
    {
        modelo.addAttribute("Usuario", new Usuario());
        return "login";
    }

    @PostMapping("/createUser")
    public String createUser(@ModelAttribute Usuario nuevo, @RequestParam(value="PRUEBA", required=false) String prueba, Model modelo) 
    {   
        if(nuevo.getUsername().isEmpty())
            return "redirect:/login?noUser";
        if(nuevo.getPassword().isEmpty())
            return "redirect:/login?noPass";

        Usuario user = new Usuario();
        user.setUsername(nuevo.getUsername());
        user.setPassword(passwordEncoder.encode(nuevo.getPassword()));
        if(prueba != null)
        {
            System.out.println("PRUEBA");
            user.setAutoridad("PRUEBA");
            user.setAutoridad("USER");
        }
        else
        {
            System.out.println("SIN PRUEBA");
            user.setAutoridad("USER");
        }
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        

        if(us.guardarUsuario(user)==null)
            return "redirect:/login?exists";
        
        return "redirect:/login?created";
    }
}
