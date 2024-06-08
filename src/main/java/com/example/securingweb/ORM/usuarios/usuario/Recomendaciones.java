package com.example.securingweb.ORM.usuarios.usuario;

import java.util.List;

import com.example.securingweb.ORM.cursos.curso.Curso;

public class Recomendaciones 
{
    private List<Curso> cursosRecomendados;

    public Recomendaciones(List<Curso> cursosRecomendados) 
    {
        this.cursosRecomendados = cursosRecomendados;
    }

    public List<Curso> getCursosRecomendados() 
    {
        return cursosRecomendados;
    }
}
