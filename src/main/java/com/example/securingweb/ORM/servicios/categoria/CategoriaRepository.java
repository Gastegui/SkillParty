package com.example.securingweb.ORM.servicios.categoria;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Repositorio de Spring Data JPA para la entidad Usuario
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    Optional<Categoria> findByDescripcion(String descripcion);
}