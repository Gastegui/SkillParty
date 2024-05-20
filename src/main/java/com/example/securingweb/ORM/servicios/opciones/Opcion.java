package com.example.securingweb.ORM.servicios.opciones;

import java.math.BigDecimal;
import com.example.securingweb.ORM.servicios.servicio.Servicio;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "opciones")
public class Opcion 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "servicio_id")
    private Servicio padre;

    private String descripcion;
    private BigDecimal precio;

    public void setPadre(Servicio p){padre=p;}
    public void setDescripcion(String d){descripcion=d;}
    public void setPrecio(BigDecimal p){precio=p;}

    public Servicio getPadre(){return padre;}
    public String getDescripcion(){return descripcion;}
    public BigDecimal getPrecio(){return precio;}

    @Override
    public boolean equals(Object o)
    {   
        if(o == null)
            return false;
        
        if(o.getClass() != this.getClass())
            return false;

        if(((Opcion) o).getDescripcion() != this.getDescripcion())
            return false;
        
        return true;
    }
}
