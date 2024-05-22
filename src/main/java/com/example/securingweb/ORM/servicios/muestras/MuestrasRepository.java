package com.example.securingweb.ORM.servicios.muestras;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MuestrasRepository extends JpaRepository<Muestra, Long> 
{
    
}
