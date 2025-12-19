package com.tfm.edcuaweb.service;

import com.tfm.edcuaweb.config.NotificationHandler;
import com.tfm.edcuaweb.dto.NotificationRequest;
import com.tfm.edcuaweb.model.Course;
import com.tfm.edcuaweb.model.Notification;
import com.tfm.edcuaweb.model.NotificationType;
import com.tfm.edcuaweb.model.User;
import com.tfm.edcuaweb.repository.CourseRepository;
import com.tfm.edcuaweb.repository.NotificationRepository;

import com.tfm.edcuaweb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepo;
    private final NotificationHandler notifyHandler;
    private final UserRepository userRepo;
    private final CourseRepository courseRepo;

    //ENviar las notificaciones
    public void sendToAll (String message){
        notifyHandler.broadcast(message);
    }

    public void sendToCourse(Course course, User sender, String title, String msg, NotificationType type){
        // Notificar al profesor del curso
        User teacher = course.getTeacher();
        sendToUser(sender, teacher, title, msg, type);
        // Notificar a cada estudiante
        course.getStudents().forEach(student -> {
            sendToUser(sender, student, title, msg, type);
        });
    }
    //Enviar al usuario
    public Notification sendToUser(User sender, User recipient, String title, String msg, NotificationType type) {
        Notification n = notificationRepo.save(Notification.builder()
                .sender(sender)
                .recipient(recipient)
                .title(title)
                .message(msg)
                .type(type)
                .build()
        );
        // Envío en tiempo real
        notifyHandler.sendToUser(recipient.getId(),n);
        return n;
    }
    //Enviar una notificacion
    public Notification sendNotification(User sender, NotificationRequest req) {
        // Validar campos obligatorios
        if (req.getTitle() == null || req.getTitle().isBlank()) {
            throw new IllegalArgumentException("El título no puede estar vacío");
        }
        if (req.getMessage() == null || req.getMessage().isBlank()) {
            throw new IllegalArgumentException("El mensaje no puede estar vacío");
        }
        if (req.getType() == null) {
            throw new IllegalArgumentException("Debes indicar un NotificationType válido");
        }
        Notification saved = null;

        // 1. Enviar a un usuario concreto
        if (req.getUserId() != null) {
            User recipient = userRepo.findById(req.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            saved = sendToUser(
                    sender,
                    recipient,
                    req.getTitle(),
                    req.getMessage(),
                    req.getType()
            );
            return saved;
        }

        //  2. Enviar a un curso completo
        if (req.isBroadcast() && req.getCourseId() != null) {
            Course course = courseRepo.findById(req.getCourseId())
                    .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado"));

            sendToCourse(
                    course,
                    sender,
                    req.getTitle(),
                    req.getMessage(),
                    req.getType()
            );
            // Como se envían muchas, no devolvemos 1 notificación
            return null;
        }
        throw  new IllegalArgumentException("Debes indicar userId, courseId o broadcast=true");
    }

    //Notifiacion de NO LEIDO
    public List<Notification> getUnread(User user) {
        return notificationRepo.findByRecipientAndReadFalse(user);
    }
    //Marcar como LEIDO
    public void markAsRead(Long id) {
        notificationRepo.findById(id).ifPresent(notify -> {
            notify.setRead(true);
            notificationRepo.save(notify);
        });
    }

}
