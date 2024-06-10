package com.example.securingweb.DTO;

import java.util.Date;

public class MensajesDTO {
    private Long id;
    private String contacto;
    private String usuario;
    private Date fechaEnvio;
    private String Texto;
    private String usuarioImagen;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getContacto() {
        return contacto;
    }
    public void setContacto(String contacto) {
        this.contacto = contacto;
    }
    public String getUsuario() {
        return usuario;
    }
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
    public Date getFechaEnvio() {
        return fechaEnvio;
    }
    public void setFechaEnvio(Date fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }
    public String getTexto() {
        return Texto;
    }
    public void setTexto(String texto) {
        Texto = texto;
    }
    public String getUsuarioImagen() {
        return usuarioImagen;
    }
    public void setUsuarioImagen(String usuarioImagen) {
        this.usuarioImagen = usuarioImagen;
    }
}
