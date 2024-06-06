package com.example.securingweb.ORM.contactos;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.securingweb.ORM.usuarios.usuario.Usuario;

public interface ContactosRepository extends JpaRepository<Contactos, Long> 
{
    Contactos findByClienteAndCreador(Usuario cliente, Usuario creador);
}
