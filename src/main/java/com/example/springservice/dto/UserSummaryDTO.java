package com.example.springservice.dto;

import com.example.springservice.entites.User;

public class UserSummaryDTO {
    public Integer id;
    public String name;
    public String profilePicture;

    public UserSummaryDTO(User u) {
        this.id = u.getUserId();
        this.name = u.getName();
        this.profilePicture = u.getProfilePicture();
    }
}

