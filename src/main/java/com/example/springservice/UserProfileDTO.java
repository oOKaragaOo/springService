package com.example.springservice;


public class UserProfileDTO {
    public String name;
    public String email;
    public String profile_picture;
    public String description;
    public String commission_status;

    public UserProfileDTO(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.profile_picture = user.getProfile_picture();
        this.description = user.getDescription();
        this.commission_status = user.getCommission_status().toString();
    }
}
