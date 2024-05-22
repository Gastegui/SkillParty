package com.example.securingweb.ORM.servicios.verServicios;

import java.sql.Date;
import com.example.securingweb.ORM.servicios.servicio.Servicio;
import com.example.securingweb.ORM.usuario.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ver_servicios")
public class VerServicio 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "servicio_id")
    private Servicio servicio;

    @Column(name = "fecha_de_ver")
    private Date fecha;
    
    public void setUsuario(Usuario u){usuario = u;}
    public void setServicio(Servicio s){servicio = s;}
    public void setFecha(Date f){fecha = f;}

    public Long getId(){return id;}
    public Usuario getUsuario(){return usuario;}
    public Servicio getServicio(){return servicio;}
    public Date getFecha(){return fecha;}

    @Override
    public boolean equals(Object o)
    {
        if(o == null)
            return false;
        
        if(o.getClass() != this.getClass())
            return false;

        return ((VerServicio)o).getUsuario().getUsername().equals(this.getUsuario().getUsername()) && ((VerServicio)o).getServicio().getTitulo().equals(this.getServicio().getTitulo());
    }
}
