package com.example.securingweb.ORM.servicios.categoria;

import java.util.List;
import com.example.securingweb.ORM.servicios.servicio.Servicio;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="categorias")
public class Categoria 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String descripcion;    

    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Servicio> servicios;

    public void setDescripcion(String d){descripcion=d;}
    public String getDescripcion(){return descripcion;}

    public Long getId(){return id;}

    @Override
    public boolean equals(Object o)
    {
        if(o == null)
            return false;

        if(o.getClass() != this.getClass())
            return false;

        if(((Categoria)o).getDescripcion() != this.getDescripcion())
            return false;

        return true;
    }
}