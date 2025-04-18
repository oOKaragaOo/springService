package com.example.springservice.dto;

import com.example.springservice.entites.Notification;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter

public class NotificationDTO {
    public String type;
    public String message;
    public String actorName;
    public Boolean isRead;
    public LocalDateTime createdAt;

    public NotificationDTO(Notification noti) {
        this.type = noti.getType().name();
        this.message = noti.getMessage();
        this.isRead = noti.getIsRead();
        this.createdAt = noti.getCreatedAt();
        this.actorName = noti.getActor() != null ? noti.getActor().getName() : "System";
    }
}


