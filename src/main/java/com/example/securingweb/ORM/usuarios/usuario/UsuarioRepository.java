package com.example.securingweb.ORM.usuarios.usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repositorio de Spring Data JPA para la entidad Usuario
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> 
{
    // Método para buscar un usuario por su nombre de usuario
    Usuario findByUsername(String username);
}
