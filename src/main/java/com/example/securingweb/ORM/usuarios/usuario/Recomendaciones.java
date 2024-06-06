package com.example.securingweb.ORM.usuarios.usuario;

import java.util.List;

import com.example.securingweb.ORM.servicios.servicio.Servicio;

public class Recomendaciones 
{
    //private List<Curso> cursosRecomendados;
    private List<Servicio> serviciosRecomendados;

    public Recomendaciones(/*List<Curso> cursosRecomendados, */List<Servicio> serviciosRecomendados) 
    {
        //this.cursosRecomendados = cursosRecomendados;
        this.serviciosRecomendados = serviciosRecomendados;
    }

    /*public List<Curso> getCursosRecomendados() 
    {
        return cursosRecomendados;
    }*/

    public List<Servicio> getServiciosRecomendados() 
    {
        return serviciosRecomendados;
    }
}
