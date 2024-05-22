package com.example.securingweb.ORM.servicios.verServicios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerServicioRepository extends JpaRepository<VerServicio, Long> 
{
    
}
