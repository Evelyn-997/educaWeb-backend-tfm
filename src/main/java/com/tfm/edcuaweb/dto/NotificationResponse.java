package com.tfm.edcuaweb.dto;

import com.tfm.edcuaweb.model.Notification;

import java.time.LocalDateTime;

public class NotificationResponse {
    private Long id;
    private String title;
    private String message;
    private String type;
    private String senderName;
    private boolean read;
    private LocalDateTime createdAt;

    public NotificationResponse(Notification n) {
        this.id = n.getId();
        this.title = n.getTitle();
        this.message = n.getMessage();
        this.type = n.getType().name();
        this.senderName = n.getSender() != null ? n.getSender().getName() : "Sistema";
        this.read = n.isRead();
        this.createdAt = n.getCreatedAt();
    }
}
