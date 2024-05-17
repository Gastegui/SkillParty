package com.example.securingweb.ORM.ficheros;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FicheroRepository extends JpaRepository<Fichero, Long>
{
    //FindAllById no hay que hacerlo porque ya forma parte de JpaRepository
    Fichero findByDireccion(String direccion);
}
