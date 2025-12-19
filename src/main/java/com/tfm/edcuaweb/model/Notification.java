package com.tfm.edcuaweb.model;


import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name= "notificaciones")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class Notification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notify_id")
    private Long id;
    private String title;
    private String message;
    @Enumerated(EnumType.STRING)
    private NotificationType type; // e.g. MANUAL, DOCUMENT_UPLOAD, NEW_TASK
    @ManyToOne
    private User sender;
    @ManyToOne
    private User recipient;
    @Column(name = "is_read")
    private boolean read;
    private boolean delivered;
    private LocalDateTime createdAt = LocalDateTime.now();


}
