package com.example.securingweb.ORM.usuario.autoridad;

import java.util.List;

import com.example.securingweb.ORM.usuario.Usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

// Clase de entidad para la tabla "autoridad"
@Entity
@Table(name = "autoridades")
public class Autoridad 
{
    // Atributos de la entidad Usuario
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(unique=true)
    private String autoridad;

    // Relación muchos a muchos con la entidad usuario
    @ManyToMany(mappedBy = "autoridades")
    private List<Usuario> usuarios;

    // Métodos getter y setter para los atributos de la clase

    public Long getId() 
    {
        return id;
    }

    public void setId(Long id) 
    {
        this.id = id;
    }

    public String getAutoridad() 
    {
        return autoridad;
    }

    public void setAutoridad(String autoridad) 
    {
        this.autoridad = autoridad;
    }

    public List<Usuario> getUsuarios() 
    {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) 
    {
        this.usuarios = usuarios;
    }
}