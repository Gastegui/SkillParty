package com.example.securingweb.ORM.curso;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Short> 
{
    // Método para buscar un curso por su nombre
    Curso findByTittle(String tittle);
}