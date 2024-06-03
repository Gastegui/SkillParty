package com.example.securingweb.ORM.servicios.servicio;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Long> 
{
    // Método para buscar un servicio por su nombre
    Optional<Servicio> findByTitulo(String titulo);
    Page<Servicio> findAll(Pageable pageable);
    Page<Servicio> findAllByIdioma_id(Long idiomaId, Pageable pageable);
    Page<Servicio> findAllByIdioma_idAndPublicado(Long idiomaId, boolean publicado, Pageable pageable);
    Page<Servicio> findAllByPublicado(boolean publicado, Pageable pageable);
    List<Servicio> findAllByPublicadoTrueAndCreadorId(Long creadorId);
    List<Servicio> findAllByPublicadoFalseAndCreadorId(Long creadorId);
}