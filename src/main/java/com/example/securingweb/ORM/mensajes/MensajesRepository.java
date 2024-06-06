package com.example.securingweb.ORM.mensajes;

import java.util.List;
//import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.securingweb.ORM.usuarios.usuario.Usuario;

public interface MensajesRepository extends JpaRepository<Mensajes, Long> {
    List<Mensajes> findByUsuario(Usuario usuario);

    List<Mensajes> findByUsuarioAndContactoClienteOrderByFechaEnvioAsc(Usuario usuario, Usuario cliente);
    List<Mensajes> findByUsuarioAndContactoCreadorOrderByFechaEnvioAsc(Usuario usuario, Usuario creador);
}
