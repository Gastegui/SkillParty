package com.example.securingweb.ORM.cursos.tipos;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tipos")
public class Tipo 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descripcion;

    public Long getId()
    {
        return id;
    }

    public String getDescripcion()
    {
        return descripcion;
    }

    
    public void setDescripcion(String d)
    {
        descripcion = d;
    }


    @Override
    public boolean equals(Object o)
    {
        if(o == null)
            return false;

        if(o.getClass() != this.getClass())
            return false;

        return ((Tipo)o).getDescripcion().equals(this.getDescripcion());
    }
}
