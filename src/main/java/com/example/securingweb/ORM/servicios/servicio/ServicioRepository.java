package com.example.securingweb.ORM.servicios.servicio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Long> 
{
    // Método para buscar un servicio por su nombre
    Servicio findByTitulo(String titulo);
}