package com.example.securingweb.ORM.servicio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Short> 
{
    // Método para buscar un servicio por su nombre
    Servicio findByTittle(String tittle);
}