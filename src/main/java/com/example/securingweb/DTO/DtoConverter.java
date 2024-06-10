package com.example.securingweb.DTO;

import com.example.securingweb.ORM.mensajes.Mensajes;
import com.example.securingweb.ORM.servicios.valorarServicios.ValorarServicios;

public class DtoConverter {
    public static ValoracionDTO convertToDto(ValorarServicios valoracion) {
        ValoracionDTO dto = new ValoracionDTO();
        dto.setId(valoracion.getId());
        dto.setUsuarioNombre(valoracion.getUsuario().getNombre());
        dto.setUsuarioApellidos(valoracion.getUsuario().getApellidos());
        dto.setComentario(valoracion.getComentario());
        dto.setValoracion(valoracion.getValoracion());
        dto.setServicioTitulo(valoracion.getServicio().getTitulo());
        dto.setUsuarioImagen("/uploads/" + valoracion.getUsuario().getId() + "/profile" + valoracion.getUsuario().getImagen().getExtension());
        return dto;
    }

    public static MensajesDTO convertToDto(Mensajes message) {
        MensajesDTO dto = new MensajesDTO();
        dto.setId(message.getId());
        dto.setContacto(message.getContacto().getCliente().getUsername());
        dto.setUsuario(message.getUsuario().getNombre());
        dto.setFechaEnvio(message.getFechaEnvio());
        dto.setTexto(message.getTexto()); 
        dto.setUsuarioImagen("/uploads/" + message.getUsuario().getId() + "/profile" + message.getUsuario().getImagen().getExtension());
        return dto;
    }
}