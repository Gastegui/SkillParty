package com.example.securingweb.Controladores;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

//import com.example.securingweb.ORM.contactos.ContactoService;
import com.example.securingweb.ORM.mensajes.Mensajes;
import com.example.securingweb.ORM.mensajes.MensajesService;
import com.example.securingweb.ORM.usuarios.usuario.Usuario;
import com.example.securingweb.ORM.usuarios.usuario.UsuarioService;

import java.security.Principal;
import java.util.List;

@Controller
public class ChatController {

    @Autowired
    private MensajesService mensajesService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private WebSocketController webSocketController;

    @GetMapping("/chat")
    public String chat(@RequestParam(value = "recipient", required = false) String recipient, Model model, Principal principal) {
        String username = principal.getName();
        List<Mensajes> mensajes = List.of();

        if (recipient != null && !recipient.isEmpty()) {
            mensajes = mensajesService.getMensajesBetweenUsers(username, recipient);
            model.addAttribute("selectedRecipient", recipient);
        }

        List<Usuario> usuarios = usuarioService.obtenerTodosLosUsuarios();
        model.addAttribute("mensajes", mensajes);
        model.addAttribute("usuarios", usuarios);
        return "chat";
    }

    @PostMapping("/send")
    public String sendMessage(@RequestParam("message") String message,
                                @RequestParam("recipient") String recipient,
                                Principal principal) {
        String username = principal.getName();

        Mensajes mensaje = mensajesService.saveMessage(username, recipient, message);

        webSocketController.notifyNewMessage(mensaje);

        return "redirect:/chat?recipient=" + recipient;
    }
}