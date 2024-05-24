package com.example.securingweb.ORM.servicios.comprarServicios;

import java.util.Date;
import com.example.securingweb.ORM.servicios.opciones.Opcion;
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
@Table(name = "comprar_servicios")
public class ComprarServicio 
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

    @ManyToOne
    @JoinColumn(name = "opcion_id") 
    private Opcion opcionCompra;
    
    @Column(name = "fecha_de_compra")
    private Date fecha;

    private boolean terminado;


    public void setUsuario(Usuario u){usuario = u;}
    public void setServicio(Servicio s){servicio = s;}
    public void setOpcionCompra(Opcion o){opcionCompra = o;}
    public void setFecha(Date f){fecha = f;}
    public void setTerminado(boolean t){terminado=t;}

    public Long getId(){return id;}
    public Usuario getUsuario(){return usuario;}
    public Servicio getServicio(){return servicio;}
    public Opcion getOpcionCompra(){return opcionCompra;}
    public Date getFecha(){return fecha;}
    public boolean getTerminado(){return terminado;}

    @Override
    public boolean equals(Object o)
    {
        if(o == null)
            return false;
        
        if(o.getClass() != this.getClass())
            return false;

        return ((ComprarServicio)o).getUsuario().getUsername().equals(this.getUsuario().getUsername()) && ((ComprarServicio)o).getServicio().getTitulo().equals(this.getServicio().getTitulo());

    }
}
