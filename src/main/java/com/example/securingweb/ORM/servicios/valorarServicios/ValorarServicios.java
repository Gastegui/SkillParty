package com.example.securingweb.ORM.servicios.valorarServicios;

import java.util.Date;
import com.example.securingweb.ORM.servicios.servicio.Servicio;
import com.example.securingweb.ORM.usuarios.usuario.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "valorar_servicios")
public class ValorarServicios 
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

    private Long valoracion;
    private String comentario;
    @Column(name = "fecha_valoracion")
    private Date fecha;


    public void setUsuario(Usuario u){usuario=u;}
    public void setServicio(Servicio s){servicio=s;}
    public void setValoracion(Long v){valoracion = v;}
    public void setComentario(String c){comentario = c;}
    public void setFecha(Date f){fecha = f;}

    public Long getId(){return id;}
    public Usuario getUsuario(){return usuario;}
    public Servicio getServicio(){return servicio;}
    public Long getValoracion(){return valoracion;}
    public String getComentario(){return comentario;}
    public Date getFecha(){return fecha;}
    
    @Override
    public boolean equals(Object o)
    {
        if(o == null)
            return false;
        
        if(o.getClass() != this.getClass())
            return false;

        return ((ValorarServicios)o).getUsuario().getUsername().equals(this.getUsuario().getUsername()) && ((ValorarServicios)o).getServicio().getTitulo().equals(this.getServicio().getTitulo());
    }
}
