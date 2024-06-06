package com.example.securingweb.ORM.cursos.tipos;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoRepository extends JpaRepository<Tipo, Long>
{
    Optional<Tipo> findByDescripcion(String descripcion);
}
