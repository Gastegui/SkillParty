package com.example.securingweb.ORM.cursos.valorarCursos;

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
@Table(name = "valorar_cursos")
public class ValorarCurso 
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

    private Long valoracion;
    private String comentario;

    @Column(name = "fecha_valoracion")
    private Date fecha;

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

    public Long getValoracion()
    {
        return valoracion;
    }

    public String getComentario()
    {
        return comentario;
    }

    public Date getFecha()
    {
        return fecha;
    }


    public void setUsuario(Usuario u)
    {
        usuario = u;
    }

    public void setCurso(Curso c)
    {
        curso = c;
    }

    public void setValoracion(Long v)
    {
        valoracion = v;
    }

    public void setComentario(String c)
    {
        comentario = c;
    }

    public void setFecha(Date f)
    {
        fecha = f;
    }


    @Override
    public boolean equals(Object o)
    {
        if(o == null)
            return false;
        
        if(o.getClass() != this.getClass())
            return false;

        return ((ValorarCurso)o).getUsuario().getUsername().equals(this.getUsuario().getUsername()) && ((ValorarCurso)o).getCurso().getTitulo().equals(this.getCurso().getTitulo());
    }
}
