package com.example.securingweb.Controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.securingweb.ORM.mensajes.Mensajes;
import com.example.securingweb.ORM.servicios.valorarServicios.ValorarServicios;
import com.example.securingweb.DTO.ValoracionDTO;
import com.example.securingweb.DTO.DeleteRatingNotification;
import com.example.securingweb.DTO.DtoConverter;
import com.example.securingweb.DTO.MensajesDTO;

@Controller
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate template;


    public void notifyNewRating(ValorarServicios valoracion) {
        ValoracionDTO valoracionDTO = DtoConverter.convertToDto(valoracion);
        template.convertAndSend("/topic/ratings", valoracionDTO);
    }

    public void notifyDeleteRating(Long ratingId) {
        DeleteRatingNotification notification = new DeleteRatingNotification(ratingId);
        template.convertAndSend("/topic/ratings/delete", notification);
    }
    
    public void notifyNewMessage(Mensajes message) {
        MensajesDTO mensajesDTO = DtoConverter.convertToDto(message);
        template.convertAndSendToUser(message.getContacto().getCliente().getUsername(), "/queue/messages", mensajesDTO);
        template.convertAndSendToUser(message.getUsuario().getUsername(), "/queue/messages", mensajesDTO);
    }
}