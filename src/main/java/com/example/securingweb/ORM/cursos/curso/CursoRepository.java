package com.example.securingweb.ORM.cursos.curso;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> 
{
    // Método para buscar un curso por su nombre
    Optional<Curso> findByTitulo(String titulo);
    Page<Curso> findAll(Pageable pageable);
    Page<Curso> findAllByIdioma_id(Long idiomaId, Pageable pageable);
    Page<Curso> findAllByIdioma_idAndPublicado(Long idiomaId, boolean publicado, Pageable pageable);
    Page<Curso> findAllByPublicado(boolean publicado, Pageable pageable);
    List<Curso> findAllByPublicadoTrueAndCreadorId(Long creadorId);
    List<Curso> findAllByPublicadoFalseAndCreadorId(Long creadorId);
}