package com.example.securingweb.DTO;

public class ValoracionDTO {
    private Long id;
    private String usuarioNombre;
    private String usuarioApellidos;
    private String comentario;
    private Long valoracion;
    private String servicioTitulo;
    private String usuarioImagen;
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUsuarioNombre() {
        return usuarioNombre;
    }
    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
    }
    public String getUsuarioApellidos() {
        return usuarioApellidos;
    }
    public void setUsuarioApellidos(String usuarioApellidos) {
        this.usuarioApellidos = usuarioApellidos;
    }
    public String getComentario() {
        return comentario;
    }
    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
    public Long getValoracion() {
        return valoracion;
    }
    public void setValoracion(Long valoracion) {
        this.valoracion = valoracion;
    }
    public String getServicioTitulo() {
        return servicioTitulo;
    }
    public void setServicioTitulo(String servicioTitulo) {
        this.servicioTitulo = servicioTitulo;
    }
    public String getUsuarioImagen() {
        return usuarioImagen;
    }
    public void setUsuarioImagen(String usuarioImagen) {
        this.usuarioImagen = usuarioImagen;
    }
}