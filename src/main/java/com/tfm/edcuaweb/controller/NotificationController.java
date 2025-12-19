package com.tfm.edcuaweb.controller;

import com.tfm.edcuaweb.dto.NotificationRequest;
import com.tfm.edcuaweb.dto.NotificationResponse;
import com.tfm.edcuaweb.model.Notification;
import com.tfm.edcuaweb.model.User;
import com.tfm.edcuaweb.repository.UserRepository;
import com.tfm.edcuaweb.service.NotificationService;
import com.tfm.edcuaweb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationService notifyService;
    private final UserService userService;
    private final UserRepository userRepo;

    @PostMapping("/send")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> sendNotification(
            @AuthenticationPrincipal User sender,
            @RequestBody NotificationRequest req
    ) {
        Notification n = notifyService.sendNotification(sender, req);

        if (n != null) {
            return ResponseEntity.ok(new NotificationResponse(n));
        }
        return ResponseEntity.ok("Notificaciones enviadas correctamente.");
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody NotificationRequest req, @AuthenticationPrincipal User sender) {
        for (Long id: req.getRecipients()) {
            User recipient =userRepo.getById(id);
            notifyService.sendToUser(sender,recipient, req.getTitle(), req.getMessage(), req.getType());
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unread")
    public List<Notification> unread(@AuthenticationPrincipal User user){
        return notifyService.getUnread(user);
    }

    @PostMapping("/{id}/mark-read")
    public void markRead(@PathVariable Long id){
        notifyService.markAsRead(id);

    }
}
