package com.example.securingweb.ORM.mensajes;

import jakarta.persistence.*;
import java.util.Date;

import com.example.securingweb.ORM.contactos.Contactos;
import com.example.securingweb.ORM.ficheros.Fichero;
import com.example.securingweb.ORM.usuarios.usuario.Usuario;

@Entity
@Table(name = "mensajes")
public class Mensajes 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "contacto_id", nullable = false)
    private Contactos contacto;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private String texto;

    @Column(name = "fecha_envio", nullable = false)
    private Date fechaEnvio;

    @ManyToOne
    @JoinColumn(name = "fichero_id")
    private Fichero fichero;

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Contactos getContacto() {
        return contacto;
    }

    public void setContacto(Contactos contacto) {
        this.contacto = contacto;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario2) {
        this.usuario = usuario2;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public Date getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(Date fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public Fichero getFichero() {
        return fichero;
    }

    public void setFichero(Fichero fichero) {
        this.fichero = fichero;
    }
}
