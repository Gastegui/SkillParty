package com.example.securingweb.Configuraciones;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.example.securingweb.Handlers.ChatWebSocketHandler;

@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
//Metodos para manejar los manejadores de WebSocket
public class WebSocketConfig implements WebSocketConfigurer, WebSocketMessageBrokerConfigurer 
{
    //Añade los manejadores de WebSocket para diferentes rutas
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) 
    {
        registry.addHandler(chatWebSocketHandler(), "/chat-websocket").setAllowedOrigins("*");
    }

    //Define el manejador de WebSocket del Chat
    @Bean
    public ChatWebSocketHandler chatWebSocketHandler() 
    {
        return new ChatWebSocketHandler();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket-endpoint").withSockJS();
    }
}