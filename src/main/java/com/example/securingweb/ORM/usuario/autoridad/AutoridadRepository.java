package com.example.securingweb.ORM.usuario.autoridad;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repositorio de Spring Data JPA para la entidad Autoridad
@Repository
public interface AutoridadRepository extends JpaRepository<Autoridad, Long> 
{
    // Método para buscar una autoridad por su nombre
    public Autoridad findByAutoridad(String autoridad);
}
