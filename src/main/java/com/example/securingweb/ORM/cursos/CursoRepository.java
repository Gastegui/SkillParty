/*package com.example.securingweb.ORM.cursos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.securingweb.ORM.cursos.Curso;
import java.util.List;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> 
{
    List<Curso> findByCategoriaAndPuntuacionGreaterThan(String categoria, int puntuacion);
    List<Curso> findByCategoriaAndPuntuacion(String categoria, int puntuacion);
    List<Curso> findByCategoriaAndPuntuacionLessThan(String categoria, int puntuacion);
}
*/