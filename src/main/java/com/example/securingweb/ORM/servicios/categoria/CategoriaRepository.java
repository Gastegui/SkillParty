package com.example.securingweb.ORM.servicios.categoria;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


// Repositorio de Spring Data JPA para la entidad Usuario
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    Categoria findByDescripcion(String descripcion);
}