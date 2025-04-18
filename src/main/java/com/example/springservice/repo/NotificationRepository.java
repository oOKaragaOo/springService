package com.example.springservice.repo;

import com.example.springservice.entites.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findAllByUser_UserIdOrderByCreatedAtDesc(Integer userId);
}
