package com.example.securingweb.ORM.curso;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.util.Date;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "Cursos")
public class Curso 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cursoID")
    private Short cursoID;

    private String titulo;
    private String descripcion;
    private Date fechaDeCreacion;
    //private Long creadorID;
    //private Short tipoID;
    private Integer precio;

    public Short getCursoId() 
    {
        return cursoID;
    }

    public void setCursoId(Short cursoID) 
    {
        this.cursoID = cursoID;
    }

    public String getTitulo() 
    {
        return titulo;
    }

    public void setTitulo(String titulo) 
    {
        this.titulo = titulo;
    }

    public String getDescripcion() 
    {
        return descripcion;
    }

    public void setDescripcion(String descripcion) 
    {
        this.descripcion = descripcion;
    }

    public Date getFechaDeCreacion() 
    {
        return fechaDeCreacion;
    }

    public void setFechaDeCreacion(Date fechaDeCreacion) 
    {
        this.fechaDeCreacion = fechaDeCreacion;
    }

    /*public Long getCreadorID() 
    {
        return creadorID;
    }

    public void setCreadorID(Long creadorID) 
    {
        this.creadorID = creadorID;
    }*/

    /*public Short getTipoID() 
    {
        return tipoID;
    }

    public void setTipoID(Short tipoID) 
    {
        this.tipoID = tipoID;
    }*/

    public Integer getPrecio() 
    {
        return precio;
    }

    public void setPrecio(Integer precio) 
    {
        this.precio = precio;
    }
}