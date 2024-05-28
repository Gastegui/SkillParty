package com.example.securingweb.ORM.ficheros;

import java.util.List;
import com.example.securingweb.ORM.servicios.servicio.Servicio;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="ficheros")
public class Fichero 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique=true)
    private String direccion;

    private String extension;

    @OneToMany(mappedBy = "portada", cascade = CascadeType.ALL)
    private List<Servicio> servicios;

    public void setDireccion(String d) {direccion=d;}
    public String getDireccion() {return direccion;}

    public void setExtension(String e) {extension=e;}
    public String getExtension() {return extension;}

    public Long getId(){return id;}

    @Override
    public boolean equals(Object o)
    {
        if(o == null)
            return false;
        if(o.getClass() != this.getClass())
            return false;
        return ((Fichero)o).getDireccion().equals(this.getDireccion());
    }
}
