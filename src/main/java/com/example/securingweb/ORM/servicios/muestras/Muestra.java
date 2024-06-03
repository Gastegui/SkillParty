package com.example.securingweb.ORM.servicios.muestras;

import com.example.securingweb.ORM.ficheros.Fichero;
import com.example.securingweb.ORM.servicios.servicio.Servicio;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "muestras")
public class Muestra 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long posicion;  
    
    @ManyToOne
    @JoinColumn(name = "servicio_id")
    private Servicio padre;
 
    @ManyToOne
    @JoinColumn(name = "multimedia_id")
    private Fichero multimedia;


    public void setPosicion(Long p){posicion=p;}
    public void setPadre(Servicio p){padre=p;}
    public void setMultimedia(Fichero m){multimedia=m;}

    public Long getPosicion(){return posicion;}
    public Servicio getPadre(){return padre;}
    public Fichero getMultimedia(){return multimedia;}
    public Long getId(){return id;}

    @Override
    public boolean equals(Object o)
    {
        if(o == null)
            return false;

        if(o.getClass() != this.getClass())
            return false;

        return ((Muestra)o).getPosicion().equals(this.getPosicion());
    }
}
