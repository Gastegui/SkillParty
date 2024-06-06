package com.example.securingweb.ORM.cursos.curso;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import com.example.securingweb.ORM.cursos.comprarCursos.ComprarCurso;
import com.example.securingweb.ORM.cursos.elemento.Elemento;
import com.example.securingweb.ORM.cursos.tipos.Tipo;
import com.example.securingweb.ORM.cursos.valorarCursos.ValorarCurso;
import com.example.securingweb.ORM.ficheros.Fichero;
import com.example.securingweb.ORM.idiomas.Idioma;
import com.example.securingweb.ORM.usuarios.usuario.Usuario;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

@Entity
@Table(name = "cursos")
public class Curso 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String titulo;

    private BigDecimal precio;
    private boolean publicado;
    private boolean verificado;
    private Long puntuacion;

    @Column(length = 1000)
    private String descripcion;

    @Column(name = "fecha_de_creacion")
    private Date fechaDeCreacion;

    @Column(name = "fecha_de_actualizacion")
    private Date fechaDeActualizacion;

    @ManyToOne
    @JoinColumn(name = "creador_id")
    private Usuario creador;

    @ManyToOne
    @JoinColumn(name = "portada_id")
    private Fichero portada;

    @ManyToOne
    @JoinColumn(name = "tipo_id")
    private Tipo tipo;

    @ManyToOne
    @JoinColumn(name = "idioma_id")
    private Idioma idioma;

    @OneToMany(mappedBy = "padre", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("posicion ASC")
    private List<Elemento> elementos;

    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ComprarCurso> compras;

    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ValorarCurso> valoraciones;


    public Long getId()
    {
        return id;
    }

    public String getTitulo()
    {
        return titulo;
    }

    public BigDecimal getPrecio()
    {
        return precio;
    }

    public boolean getPublicado()
    {
        return publicado;
    }

    public boolean getVerificado()
    {
        return  verificado;
    }
    
    public Long getPuntuacion()
    {
        return puntuacion;
    }

    public String getDescripcion()
    {
        return descripcion;
    }

    public Date getFechaDeCreacion()
    {
        return fechaDeCreacion;
    }

    public Date getFechaDeActualizacion()
    {
        return fechaDeActualizacion;
    }

    public Usuario getCreador()
    {
        return creador;
    }

    public Fichero getPortada()
    {
        return portada;
    }

    public Tipo getTipo()
    {
        return tipo;
    }

    public Idioma getIdioma()
    {
        return idioma;
    }

    public List<Elemento> getElementos()
    {
        return elementos;
    }

    public List<ValorarCurso> getValoraciones()
    {
        return valoraciones;
    }

    public List<ComprarCurso> getCompras()
    {
        return compras;
    }

    public void setTitulo(String t)
    {
        titulo=t;
    }

    public void setPrecio(BigDecimal p)
    {
        precio = p;
    }

    public void setPublicado(boolean p)
    {
        publicado = p;
    }

    public void setVerificado(boolean v)
    {
        verificado = v;
    }

    public void setPuntuacion(Long p)
    {
        puntuacion = p;
    }

    public void setDescripcion(String d)
    {
        descripcion = d;
    }

    public void setFechaDeCreacion(Date f)
    {
        fechaDeCreacion = f;
    }

    public void setFechaDeActualizacion(Date f)
    {
        fechaDeActualizacion = f;
    }

    public void setCreador(Usuario c)
    {
        creador = c;
    }

    public void setPortada(Fichero p)
    {
        portada = p;
    }

    public void setTipo(Tipo t)
    {
        tipo = t;
    }

    public void setIdioma(Idioma i)
    {
        idioma = i;
    }

    @Override
    public boolean equals(Object o)
    {
        if(o == null)
            return false;

        if(o.getClass() != this.getClass())
            return false;

        return ((Curso)o).getTitulo().equals(this.getTitulo());
    }
}