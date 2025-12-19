package com.tfm.edcuaweb.dto;

import com.tfm.edcuaweb.model.Notification;
import com.tfm.edcuaweb.model.NotificationType;
import lombok.Data;

import java.util.List;

@Data
public class NotificationRequest {
    private String title;
    private String message;
    private NotificationType type;
    private List<Long> recipients;
    private Long userId;      // opcional
    private Long courseId;    // opcional
    private boolean broadcast;

}
