package com.example.securingweb.Controladores;

import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.securingweb.ORM.usuario.Usuario;
import com.example.securingweb.ORM.usuario.UsuarioRepository;

@Controller
@RequestMapping("/user")
public class UserController 
{
    private UsuarioRepository usuarioRepository;

    @Autowired
    public UserController(UsuarioRepository usuarioRepository)
    {
        this.usuarioRepository = usuarioRepository;
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
}
