package com.example.securingweb.Handlers;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

//Clase que maneja los mensajes de WebSocket
public class ChatWebSocketHandler extends TextWebSocketHandler 
{
    //Conjunto de sesiones WebSocket activas, sincronizado para manejo de concurrencia
    private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());

    //Metodo que se llama cuando se establece una conexion WebSocket
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception 
    {
        sessions.add(session);
    }

    //Metodo que maneja los mensajes de texto recibidos a traves de WebSocket
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception 
    {
        for (WebSocketSession webSocketSession : sessions) 
        {
            if (webSocketSession.isOpen()) 
            {
                webSocketSession.sendMessage(message);
            }
        }
    }

    //Metodo que se llama cuando se cierra una conexion WebSocket
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
    }
}