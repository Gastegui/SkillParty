package com.example.securingweb.ORM.cursos.comprarCursos;

import java.math.BigDecimal;
import java.util.Date;
import com.example.securingweb.ORM.cursos.curso.Curso;
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
@Table(name="comprar_cursos")
public class ComprarCurso 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "curso_id")
    private Curso curso;

    @Column(name = "fecha_de_compra")
    private Date fecha;

    private BigDecimal precio;
    private boolean terminado;


    public Long getId()
    {
        return id;
    }

    public Usuario getUsuario()
    {
        return usuario;
    }

    public Curso getCurso()
    {
        return curso;
    }

    public Date getFecha()
    {
        return fecha;
    }

    public BigDecimal getPrecio()
    {
        return precio;
    }

    public boolean getTerminado()
    {
        return terminado;
    }


    public void setUsuario(Usuario u)
    {
        usuario = u;
    }

    public void setCurso(Curso c)
    {
        curso = c;
    }

    public void setFecha(Date f)
    {
        fecha = f;
    }

    public void setTerminado(boolean t)
    {
        terminado = t;
    }
     @Override
    public boolean equals(Object o)
    {
        if(o == null)
            return false;
        
        if(o.getClass() != this.getClass())
            return false;

        return ((ComprarCurso)o).getUsuario().getUsername().equals(this.getUsuario().getUsername()) && ((ComprarCurso)o).getCurso().getTitulo().equals(this.getCurso().getTitulo());

    }
}