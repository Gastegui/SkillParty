package com.example.securingweb.Controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.securingweb.ORM.usuarios.usuario.Usuario;
import com.example.securingweb.ORM.usuarios.usuario.UsuarioService;
import com.example.securingweb.ORM.cursos.curso.Curso;
import com.example.securingweb.ORM.cursos.curso.CursoRepository;
import com.example.securingweb.ORM.servicios.servicio.Servicio;
import com.example.securingweb.ORM.servicios.servicio.ServicioRepository;
import java.util.stream.Collectors;
import java.util.List;

@Controller
public class MainPageController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private ServicioRepository servicioRepository;

    private Usuario getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Usuario) (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof Usuario) ? null : authentication.getPrincipal());
    }

    @GetMapping("/")
    public String mainPage(Model model) {
        Usuario usuario = getUser();
        boolean isAuthenticated = usuario != null;
        model.addAttribute("isAuthenticated", isAuthenticated);

        if (isAuthenticated) {
            Usuario usuarioDetalles = usuarioService.findByUsername(usuario.getUsername()).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            int edad = usuarioService.calcularEdad(usuarioDetalles.getFechaDeNacimiento());
            List<Curso> cursosRecomendados = usuarioService.obtenerRecomendacionesCursos(edad);

            model.addAttribute("username", usuarioDetalles.getUsername());
            model.addAttribute("cursosRecomendados", cursosRecomendados);
        }

        // Obtener los tres primeros cursos y servicios de la lista general
        List<Curso> topCursos = cursoRepository.findAll().stream().limit(3).collect(Collectors.toList());
        List<Servicio> topServicios = servicioRepository.findAll().stream().limit(3).collect(Collectors.toList());

        model.addAttribute("topCursos", topCursos);
        model.addAttribute("topServicios", topServicios);

        return "mainPage";
    }
}
