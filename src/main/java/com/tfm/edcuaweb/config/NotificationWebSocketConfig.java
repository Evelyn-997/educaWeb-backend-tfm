package com.tfm.edcuaweb.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

//REVISAR FUNCIONAMIENTO
@Configuration
@EnableWebSocket
public class NotificationWebSocketConfig implements WebSocketConfigurer{
    @Autowired
    private NotificationHandler notificationHandler;

    @Autowired
    public NotificationWebSocketConfig(NotificationHandler handler) {
        this.notificationHandler = handler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(notificationHandler, "/ws/notifications")
                 .setAllowedOrigins("http://localhost:4200", "ws://localhost:4200");
                //.setAllowedOrigins("*");
    }
}
