package com.example.securingweb.ORM.cursos.tipos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoRepository extends JpaRepository<Tipo, Long>
{
    Tipo findByDescripcion(String descripcion);
}
