package com.example.securingweb.ORM.cursos.valorarCursos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ValorarCursoRepository extends JpaRepository<ValorarCurso, Long> 
{
    
}
