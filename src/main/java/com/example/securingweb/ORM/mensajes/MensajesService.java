package com.example.securingweb.ORM.mensajes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.securingweb.ORM.contactos.Contactos;
import com.example.securingweb.ORM.contactos.ContactosRepository;
import com.example.securingweb.ORM.usuarios.usuario.Usuario;
import com.example.securingweb.ORM.usuarios.usuario.UsuarioRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class MensajesService {

    @Autowired
    private MensajesRepository mensajesRepository;

    @Autowired
    private ContactosRepository contactosRepository;

    @Autowired
    private UsuarioRepository usuariosRepository;

    public List<Mensajes> getAllMensajes() {
        return mensajesRepository.findAll();
    }

    public List<Mensajes> getMensajesByUser(String username) 
    {
        Usuario usuario = usuariosRepository.findByUsername(username).orElse(null);
        return mensajesRepository.findByUsuario(usuario);
    }

    public List<Mensajes> getMensajesBetweenUsers(String user1Username, String user2Username) {
        Usuario user1 = usuariosRepository.findByUsername(user1Username).orElse(null);
        Usuario user2 = usuariosRepository.findByUsername(user2Username).orElse(null);

        List<Mensajes> mensajes = new ArrayList<>();
        mensajes.addAll(mensajesRepository.findByUsuarioAndContactoClienteOrderByFechaEnvioAsc(user1, user2));
        mensajes.addAll(mensajesRepository.findByUsuarioAndContactoCreadorOrderByFechaEnvioAsc(user1, user2));
        mensajes.addAll(mensajesRepository.findByUsuarioAndContactoClienteOrderByFechaEnvioAsc(user2, user1));
        mensajes.addAll(mensajesRepository.findByUsuarioAndContactoCreadorOrderByFechaEnvioAsc(user2, user1));

        mensajes.sort((m1, m2) -> m1.getFechaEnvio().compareTo(m2.getFechaEnvio()));

        return mensajes;
    }

    public Mensajes saveMessage(String senderUsername, String recipientUsername, String texto) {
        Usuario sender = usuariosRepository.findByUsername(senderUsername).orElse(null);
        Usuario recipient = usuariosRepository.findByUsername(recipientUsername).orElse(null);

        Contactos contacto = contactosRepository.findByClienteAndCreador(recipient, sender);
        if (contacto == null) {
            contacto = contactosRepository.findByClienteAndCreador(sender, recipient);
            if (contacto == null) {
                contacto = new Contactos();
                contacto.setCliente(recipient);
                contacto.setCreador(sender);
                contacto.setFecha(new Date());
                contacto.setDescripcion("Descripción del contacto");
                contacto = contactosRepository.save(contacto);
            }
        }

        Mensajes mensaje = new Mensajes();
        mensaje.setUsuario(sender);
        mensaje.setTexto(texto);
        mensaje.setFechaEnvio(new Date());
        mensaje.setContacto(contacto);

        return mensajesRepository.save(mensaje);
    }

    public Optional<Mensajes> getMensajeById(Long id) {
        return mensajesRepository.findById(id);
    }

    public Mensajes saveMensaje(Mensajes mensaje) {
        return mensajesRepository.save(mensaje);
    }

    public void deleteMensaje(Long id) {
        mensajesRepository.deleteById(id);
    }
}