package com.example.securingweb.ORM.servicios.servicio;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.util.Date;
import java.util.List;
import com.example.securingweb.ORM.ficheros.Fichero;
import com.example.securingweb.ORM.idiomas.Idioma;
import com.example.securingweb.ORM.servicios.categoria.Categoria;
import com.example.securingweb.ORM.servicios.comprarServicios.ComprarServicio;
import com.example.securingweb.ORM.servicios.muestras.Muestra;
import com.example.securingweb.ORM.servicios.opciones.Opcion;
import com.example.securingweb.ORM.servicios.valorarServicios.ValorarServicios;
import com.example.securingweb.ORM.usuarios.usuario.Usuario;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "servicios")
public class Servicio 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String titulo;
    private String descripcion;
    @Column(name="fecha_de_creacion")
    private Date fechaDeCreacion;
    @Column(name="fecha_de_actualizacion")
    private Date fechaDeActualizacion;

    private boolean publicado;

    @ManyToOne
    @JoinColumn(name="creador_id")
    private Usuario creador;

    @ManyToOne
    @JoinColumn(name="categoria_id")
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name="portada_id")
    private Fichero portada;

    @ManyToOne
    @JoinColumn(name="idioma_id")
    private Idioma idioma;

    @OneToMany(mappedBy = "padre", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("precio ASC")
    private List<Opcion> opciones;

    @OneToMany(mappedBy = "padre", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("posicion ASC")
    private List<Muestra> muestras;

    @OneToMany(mappedBy = "servicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ComprarServicio> compras;

    @OneToMany(mappedBy = "servicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ValorarServicios> valoraciones;

    
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
        this.fechaDeActualizacion = fechaDeCreacion;
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

    public List<Opcion> getOpciones()
    {
        return opciones;
    }

    public List<Muestra> getMuestras()
    {
        return muestras;
    }

    public List<ComprarServicio> getCompradores()
    {
        return compras;
    }

    public List<ValorarServicios> getValoraciones()
    {
        return valoraciones;
    }

    public Idioma getIdioma()
    {
        return idioma;
    }

    public void setIdioma(Idioma i)
    {
        idioma=i;
    }

    public void setPublicado(boolean p)
    {
        publicado = p;
    }

    public boolean getPublicado()
    {
        return publicado;
    }

    public void setFechaDeActualizacion(Date fecha)
    {
        fechaDeActualizacion = fecha;
    }

    public Date getFechaDeActualizacion()
    {
        return fechaDeActualizacion;
    }

    @Override
    public boolean equals(Object o)
    {
        if(o == null)
            return false;

        if(o.getClass() != this.getClass())
            return false;

        return ((Servicio)o).getTitulo().equals(this.getTitulo());
    }
}