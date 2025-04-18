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

    public void notifyFollow(User receiver, User actor) {
        if (receiver == null || actor == null || receiver.getUserId().equals(actor.getUserId())) return;

        Notification noti = new Notification();
        noti.setUser(receiver);
        noti.setActor(actor);
        noti.setType(Notification.NotificationType.FOLLOW);
        noti.setMessage(actor.getName() + " started following you");
        noti.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(noti);
    }

    public void notifyLike(User receiver, User actor, Integer postId) {
        if (receiver == null || actor == null || receiver.getUserId().equals(actor.getUserId())) return;

        Notification noti = new Notification();
        noti.setUser(receiver);
        noti.setActor(actor);
        noti.setType(Notification.NotificationType.LIKE);
        noti.setMessage(actor.getName() + " liked your post (ID: " + postId + ")");
        noti.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(noti);
    }

    public void notifyComment(User receiver, User actor, Integer postId, String commentContent) {
        if (receiver == null || actor == null || receiver.getUserId().equals(actor.getUserId())) return;

        Notification noti = new Notification();
        noti.setUser(receiver);
        noti.setActor(actor);
        noti.setType(Notification.NotificationType.COMMENT);
        noti.setMessage(actor.getName() + " commented on your post (ID: " + postId + "): \"" + commentContent + "\"");
        noti.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(noti);
    }

    public List<NotificationDTO> getUserNotifications(User user) {
        return notificationRepository.findAllByUser_UserIdOrderByCreatedAtDesc(user.getUserId())
                .stream()
                .map(NotificationDTO::new)
                .toList();
    }

    public long countUnreadNotifications(User user) {
        return notificationRepository.findAllByUser_UserIdOrderByCreatedAtDesc(user.getUserId())
                .stream()
                .filter(n -> !n.getIsRead())
                .count();
    }

    public void markAllAsRead(User user) {
        List<Notification> notis = notificationRepository.findAllByUser_UserIdOrderByCreatedAtDesc(user.getUserId());
        notis.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(notis);
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
