package com.example.securingweb.ORM.servicios.servicio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.util.Date;
import com.example.securingweb.ORM.ficheros.Fichero;
import com.example.securingweb.ORM.servicios.categoria.Categoria;
import com.example.securingweb.ORM.usuario.Usuario;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "servicios")
public class Servicio 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String descripcion;
    @Column(name="fecha_de_creacion")
    private Date fechaDeCreacion;

    @ManyToOne
    @JoinColumn(name="creador_id")
    private Usuario creador;

    @ManyToOne
    @JoinColumn(name="categoria_id")
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name="portada_id")
    private Fichero portada;

    public Long getId() 
    {
        return id;
    }

    public void setId(Long servicioID) 
    {
        this.id = servicioID;
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

    public Fichero getPortada()
    {
        return portada;
    }

    public void setPortada(Fichero p)
    {
        portada=p;
    }


    public Categoria getCategoria()
    {
        return categoria;
    }

    public void setCategoria(Categoria c)
    {
        categoria = c;
    }
    
    public Usuario getCreador()
    {
        return creador;
    }

    public void setCreador(Usuario c)
    {
        creador = c;
    }

    @Override
    public boolean equals(Object o)
    {
        if(o == null)
            return false;

        if(o.getClass() != this.getClass())
            return false;

        if(((Servicio)o).getTitulo() != this.getTitulo())
            return false;
        
        return true;
    }
}