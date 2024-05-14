package com.example.securingweb.ORM.curso;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CursoService 
{
    private CursoRepository cursoRepository;

    @Autowired
    public CursoService(CursoRepository cursoRepository) 
    {
        this.cursoRepository = cursoRepository;
    }

    public List<Curso> obtenerTodosLosCursos() 
    {
        return cursoRepository.findAll();
    }

    public Curso guardarCurso(Curso curso) 
    {
        return cursoRepository.save(curso);
    }
}
