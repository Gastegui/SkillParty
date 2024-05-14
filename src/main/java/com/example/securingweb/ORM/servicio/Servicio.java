package com.example.securingweb.ORM.servicio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.util.Date;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "Servicios")
public class Servicio 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "servicioID")
    private Short servicioID;

    private String titulo;
    private String descripcion;
    private Date fechaDeCreacion;
    //private Long creadorID;
    //private Short categoriaID;

    public Short getServicioId() 
    {
        return servicioID;
    }

    public void setServicioId(Short servicioID) 
    {
        this.servicioID = servicioID;
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

    /*public Short getCategoriaID() 
    {
        return categoriaID;
    }

    public void setCategoriaID(Short categoriaID) 
    {
        this.categoriaID = categoriaID;
    }*/
}