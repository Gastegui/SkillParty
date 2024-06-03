package com.example.securingweb.ORM.servicios.comprarServicios;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComprarServiciosRepository extends JpaRepository<ComprarServicio, Long>
{
    List<ComprarServicio> findByTerminadoFalseAndServicioCreadorId(Long creadorId);
    List<ComprarServicio> findByTerminadoFalseAndUsuarioId(Long usuarioId);
    List<ComprarServicio> findByTerminadoTrueAndUsuarioId(Long usuarioId);
}
