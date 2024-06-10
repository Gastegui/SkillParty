package com.example.securingweb.Controladores;

import java.math.BigDecimal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.securingweb.ORM.usuarios.usuario.Usuario;

import jakarta.servlet.http.HttpSession;

@ControllerAdvice
public class GlobalControllerAdvice {

    private Usuario getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Usuario) (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof Usuario) ? null : authentication.getPrincipal());
    }

    @ModelAttribute
    public void addAttributes(HttpSession session, Model model) {
        Usuario usuario = getUser();
        if(usuario != null)
        {
            Boolean isAdmin = usuario.isAdmin();
            model.addAttribute("isAdmin", isAdmin);
        }
        else
        {
            usuario = new Usuario();
            usuario.setSaldo(BigDecimal.ZERO);
        }
        
        if(usuario.getSaldo() == null)
            usuario.setSaldo(BigDecimal.ZERO);
            
        model.addAttribute("usuario", usuario);
    }
}