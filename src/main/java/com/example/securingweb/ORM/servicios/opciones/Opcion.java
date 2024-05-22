package com.example.securingweb.ORM.servicios.opciones;

import java.math.BigDecimal;
import java.util.List;
import com.example.securingweb.ORM.servicios.comprarServicios.ComprarServicio;
import com.example.securingweb.ORM.servicios.servicio.Servicio;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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

    @OneToMany(mappedBy = "opcionCompra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ComprarServicio> compras;

    public void setPadre(Servicio p){padre=p;}
    public void setDescripcion(String d){descripcion=d;}
    public void setPrecio(BigDecimal p){precio=p;}

    public Servicio getPadre(){return padre;}
    public String getDescripcion(){return descripcion;}
    public BigDecimal getPrecio(){return precio;}
    public List<ComprarServicio> getCompras(){return compras;}
    public Long getId(){return id;}

    @Override
    public boolean equals(Object o)
    {   
        if(o == null)
            return false;
        
        if(o.getClass() != this.getClass())
            return false;

        return ((Opcion) o).getDescripcion().equals(this.getDescripcion());
  
    }
}
