package com.tfm.edcuaweb.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class NotificationHandler extends TextWebSocketHandler {
    private final Map<Long, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session){

        try {
            Long userId = extractUserId(session);
            userSessions.put(userId, session);
        }catch(Exception e){
            closeSession(session);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        // si necesitas recibir mensajes
        // System.out.println("Mensaje recibido WS: " + message.getPayload());
    }

//================= Envio de notificaciones======================*/
    public void sendToUser(Long userId, Object notify) {
        try{
            WebSocketSession session = userSessions.get(userId);
            if(session!=null && session.isOpen()){
                session.sendMessage(
                        new TextMessage( new ObjectMapper().writeValueAsString(notify))
                );
            }
        }catch(Exception ignored){}
    }

    public void broadcast(Object notification){
        userSessions.values().forEach(session -> {
            try{
                if(session.isOpen()){
                    session.sendMessage(new TextMessage( mapper.writeValueAsString(notification)));
                }
            }catch(Exception ignored){}
        });
    }
    //Metodos auxiliares
    private Long extractUserId(WebSocketSession session){
        String query =  session.getUri().getQuery();
        if(query!=null && !query.startsWith("userId=")){
            throw new IllegalArgumentException("UserID no presente en WS");
        }
        return Long.parseLong(query.split("userId=")[1]);
    }
    private void closeSession(WebSocketSession session) {
        try {
            session.close();
        } catch (Exception ignored) {
        }
    }
}
