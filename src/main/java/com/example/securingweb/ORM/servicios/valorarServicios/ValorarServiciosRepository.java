package com.example.securingweb.ORM.servicios.valorarServicios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ValorarServiciosRepository extends JpaRepository<ValorarServicios, Long>
{
    
}
