package com.example.securingweb.ORM.servicios.servicio;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Long> 
{
    // Método para buscar un servicio por su nombre
    Optional<Servicio> findByTitulo(String titulo);
    Page<Servicio> findAll(Pageable pageable);
}