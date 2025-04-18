package com.example.springservice.entites;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "notifications")
public class Notification {
    // Getters/setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer notificationId;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // คนที่ได้รับแจ้งเตือน

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id")
    private User actor;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Setter
    @Column(columnDefinition = "TEXT")
    private String message;

    @Setter
    @Column(nullable = false)
    private Boolean isRead = false;

    @Setter
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum NotificationType {
        FOLLOW, LIKE, COMMENT
    }

}

