package com.tfm.edcuaweb.event;

import com.tfm.edcuaweb.service.NotificationService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {
    private final NotificationService notifyService;

    @EventListener
    public void onEvent(NotificationEvent event) {
        notifyService.sendToUser(
                event.getSender(),event.getRecipient(),event.getTitle(),event.getMessage(), event.getType());
    }
}
