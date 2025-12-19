package com.tfm.edcuaweb.event;

import com.tfm.edcuaweb.model.NotificationType;
import com.tfm.edcuaweb.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class NotificationEvent {
    private final User sender;
    private final User recipient;
    private final String title;
    private final String message;
    private final NotificationType type;
}
