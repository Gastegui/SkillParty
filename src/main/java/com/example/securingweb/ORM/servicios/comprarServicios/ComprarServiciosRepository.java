package com.example.securingweb.ORM.servicios.comprarServicios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComprarServiciosRepository extends JpaRepository<ComprarServicio, Long>
{
    
}
