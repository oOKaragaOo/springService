package com.example.springservice.service;

import com.example.springservice.dto.NotificationDTO;
import com.example.springservice.entites.Notification;
import com.example.springservice.entites.User;
import com.example.springservice.repo.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    // ✅ none Actor
    public void send(User target, Notification.NotificationType type, String message) {
        send(target, null, type, message);
    }
    // ✅ have Actor
    public void send(User target, User actor, Notification.NotificationType type, String message) {
        if (target == null || type == null) return;

        Notification noti = new Notification();
        noti.setUser(target);
        noti.setActor(actor);
        noti.setType(type);
        noti.setMessage(message);
        noti.setIsRead(false);
        noti.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(noti);
    }

    // ✅ FOLLOW
    public void notifyFollow(User receiver, User actor) {
        if (receiver == null || actor == null || receiver.getUserId().equals(actor.getUserId())) return;
        send(receiver, actor, Notification.NotificationType.FOLLOW,
                actor.getName() + " started following you");
    }

    // ✅ LIKE
    public void notifyLike(User receiver, User actor, Integer postId) {
        if (receiver == null || actor == null || receiver.getUserId().equals(actor.getUserId())) return;
        send(receiver, actor, Notification.NotificationType.LIKE,
                actor.getName() + " liked your post (ID: " + postId + ")");
    }

    // ✅ COMMENT
    public void notifyComment(User receiver, User actor, Integer postId, String commentContent) {
        if (receiver == null || actor == null || receiver.getUserId().equals(actor.getUserId())) return;
        send(receiver, actor, Notification.NotificationType.COMMENT,
                actor.getName() + " commented on your post (ID: " + postId + "): \"" + commentContent + "\"");
    }

    // ✅ Noti sorted
    public List<NotificationDTO> getUserNotifications(User user) {
        return notificationRepository.findAllByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(NotificationDTO::new)
                .toList();
    }

    public long countUnreadNotifications(User user) {
        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    public void markAllAsRead(User user) {
        List<Notification> unread = notificationRepository.findAllByUserAndIsReadFalse(user);
        unread.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unread);
    }
}


// ✅ ตัวอย่างการใช้ใน followUser()
// @Autowired
// private NotificationService notificationService;
// ...
// notificationService.notifyFollow(following, follower);

// ✅ ตัวอย่างการใช้งาน API:
// GET /notifications => getUserNotifications(user)
// POST /notifications/read => markAllAsRead(user)
