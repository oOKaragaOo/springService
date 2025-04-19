package com.example.springservice.controller;

import com.example.springservice.SessionUtil;
import com.example.springservice.dto.NotificationDTO;
import com.example.springservice.entites.User;
import com.example.springservice.repo.NotificationRepository;
import com.example.springservice.repo.UserRepository;
import com.example.springservice.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private NotificationRepository notificationRepository;


    @GetMapping
    public ResponseEntity<?> getNotifications(HttpServletRequest request) {
        User user = SessionUtil.requireSessionUser(userRepository, request);
        List<NotificationDTO> notifications = notificationService.getUserNotifications(user);
        long unreadCount = notificationService.countUnreadNotifications(user);

        return ResponseEntity.ok(Map.of(
                "notifications", notifications,
                "unreadCount", unreadCount
        ));
    }

    @PostMapping("/read")
    public ResponseEntity<?> markNotificationsAsRead(HttpServletRequest request) {
        User user = SessionUtil.requireSessionUser(userRepository, request);
        notificationService.markAllAsRead(user);
        return ResponseEntity.ok(Map.of("message", "All notifications marked as read"));
    }
    @GetMapping("/notifications")
    public ResponseEntity<?> getMyNotifications(HttpServletRequest request) {
        User user = SessionUtil.requireSessionUser(userRepository, request);
        List<NotificationDTO> notis = notificationRepository.findAllByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(NotificationDTO::new)
                .toList();
        return ResponseEntity.ok(notis);
    }
}


// ✅ ตัวอย่างการใช้งานใน PostController หรือ PostService:
// @Autowired
// private NotificationService notificationService;
//
// // หลังจาก like post สำเร็จ:
// notificationService.notifyLike(post.getAuthor(), currentUser, post.getId());
//
// // หลังจาก comment สำเร็จ:
// notificationService.notifyComment(post.getAuthor(), currentUser, post.getId(), comment.getContent());

