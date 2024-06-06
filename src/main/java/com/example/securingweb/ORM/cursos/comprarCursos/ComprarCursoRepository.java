package com.example.securingweb.ORM.cursos.comprarCursos;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComprarCursoRepository extends JpaRepository<ComprarCurso, Long>
{
    List<ComprarCurso> findByTerminadoFalseAndCursoCreadorId(Long creadorId);
    List<ComprarCurso> findByTerminadoFalseAndUsuarioId(Long usuarioId);
    List<ComprarCurso> findByTerminadoTrueAndUsuarioId(Long usuarioId);
    List<ComprarCurso> findByUsuarioUsername(String username);
    Optional<ComprarCurso> findByUsuarioUsernameAndCursoTitulo(String username, String titulo);
}
