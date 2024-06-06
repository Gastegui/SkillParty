package com.example.securingweb.Controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.securingweb.ORM.usuarios.usuario.Usuario;
import com.example.securingweb.ORM.usuarios.usuario.UsuarioService;
import com.example.securingweb.ORM.usuarios.usuario.Recomendaciones;

@Controller
public class MainPageController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * 
     * @return el usuario loggeado
     */
    private Usuario getUser()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Usuario) (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof Usuario) ? null : authentication.getPrincipal());
    }

    @GetMapping("/")
    public String mainPage(Model model) {
        // Obtener el usuario actual desde el contexto de seguridad
        /*Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = null;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else if (principal != null) {
            username = principal.toString();
        }

        if (username == null) {
            throw new RuntimeException("Usuario no autenticado");
        }*/
        if(getUser()==null)
        {
            return "mainPage";
        }
        else
        {
            // Obtener el usuario desde el nombre de usuario
            Usuario usuario = usuarioService.findByUsername(getUser().getUsername()).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Obtener recomendaciones basadas en la edad del usuario
            Recomendaciones recomendaciones = usuarioService.obtenerRecomendaciones(usuario.getId());

            // Añadir el nombre de usuario y las recomendaciones al modelo
            //model.addAttribute("username", usuario.getUsername());
            //model.addAttribute("cursosRecomendados", recomendaciones.getServiciosRecomendados()/*getCursosRecomendados()*/);
            model.addAttribute("serviciosRecomendados", recomendaciones.getServiciosRecomendados());
        }

        
        return "mainPage";
    }
}
