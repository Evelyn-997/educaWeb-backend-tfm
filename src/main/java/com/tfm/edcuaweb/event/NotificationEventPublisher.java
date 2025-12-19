package com.tfm.edcuaweb.event;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class NotificationEventPublisher {
    private final ApplicationEventPublisher publisher;

    public void publish( NotificationEvent event){
        publisher.publishEvent(event);
    }
}
