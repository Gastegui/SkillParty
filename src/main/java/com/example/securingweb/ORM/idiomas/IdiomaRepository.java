package com.example.securingweb.ORM.idiomas;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdiomaRepository extends JpaRepository<Idioma, Long> 
{
    Optional<Idioma> findByIdioma(String idioma);
}
