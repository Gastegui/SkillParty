package com.example.securingweb.ORM.idiomas;

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
@Table(name = "idiomas")
public class Idioma 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String idioma;

    @OneToMany(mappedBy="idioma", cascade = CascadeType.ALL)
    private List<Servicio> servicios;
    
    public String getIdioma(){return idioma;}
    public Long getId(){return id;}

    //OneToMany(mappedBy="idioma", cascade = CascadeType.ALL)
    //private List<Curso> cursos;

    @Override
    public boolean equals(Object o)
    {
        if(o == null)
            return false;

        if(o.getClass() != this.getClass())
            return false;

        return ((Idioma)o).getIdioma().equals(this.getIdioma());
    }
}
