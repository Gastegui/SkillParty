package com.example.securingweb.ORM.cursos.elemento;

import com.example.securingweb.ORM.cursos.curso.Curso;
import com.example.securingweb.ORM.ficheros.Fichero;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "elementos")
public class Elemento 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "curso_id")
    private Curso padre;

    private String titulo;
    private Long posicion;

    @ManyToOne
    @JoinColumn(name = "multimedia_id")
    private Fichero multimedia;

    @Column(length = 500)
    private String texto;

    @ManyToOne
    @JoinColumn(name = "fichero_id")
    private Fichero adjunto;

    public Long getId()
    {
        return id;
    }

    public Curso getPadre()
    {
        return padre;
    }

    public String getTitulo()
    {
        return titulo;
    }

    public Long getPosicion()
    {
        return posicion;
    }

    public Fichero getMultimedia()
    {
        return multimedia;
    }

    public String getTexto()
    {
        return texto;
    }

    public Fichero getAdjunto()
    {
        return adjunto;
    }


    public void setPadre(Curso p)
    {
        padre = p;
    }

    public void setTitulo(String t)
    {
        titulo = t;
    }

    public void setPosicion(Long p)
    {
        posicion = p;
    }

    public void setMultimedia(Fichero m)
    {
        multimedia = m;
    }

    public void setTexto(String t)
    {
        texto = t;
    }

    public void setAdjunto(Fichero a)
    {
        adjunto = a;
    }


    @Override
    public boolean equals(Object o)
    {
        if(o == null)
            return false;

        if(o.getClass() != this.getClass())
            return false;

        return ((Elemento)o).getPosicion().equals(this.getPosicion());
    }
}