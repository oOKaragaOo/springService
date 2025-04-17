package com.example.springservice.dto;

import com.example.springservice.entites.Post;

import java.time.LocalDateTime;

public class PostResponseDTO {
    public Integer id;
    public String caption;
    public String imageUrl;
    public String authorName;
    public Integer likeCount;
    public Integer commentCount;
    public LocalDateTime createdAt;

    public PostResponseDTO(Post post) {
        this.id = post.getId();
        this.caption = post.getCaption();
        this.imageUrl = post.getImageUrl();
        this.authorName = post.getAuthor().getName();
        this.likeCount = post.getLikes().size();
        this.commentCount = post.getComments().size();
        this.createdAt = post.getCreatedAt();
    }
}
