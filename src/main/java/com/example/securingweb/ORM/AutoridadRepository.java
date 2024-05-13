package com.example.securingweb.ORM;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AutoridadRepository extends JpaRepository<Autoridad, Long> 
{
    public Autoridad findByAutoridad(String autoridad);
}
