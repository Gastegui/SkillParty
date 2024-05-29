package com.example.securingweb.ORM.usuarios.usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

// Repositorio de Spring Data JPA para la entidad Usuario
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> 
{
    // Método para buscar un usuario por su nombre de usuario
    Usuario findByUsername(String username);

    @Procedure(procedureName = "descontarSaldoCurso")
    void descontarSaldoCurso(@Param("usuario_id") Long usuarioId, @Param("curso_id") Long cursoId);

    @Procedure(procedureName = "descontarSaldoServicio")
    void descontarSaldoServicio(@Param("usuario_id") Long usuarioId, @Param("servicio_id") Long servicioId, @Param("opcion_id") Long opcionId);
}
