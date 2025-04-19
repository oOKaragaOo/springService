package com.example.springservice.dto;

import com.example.springservice.entites.Comment;
import com.example.springservice.entites.Post;

import java.time.LocalDateTime;
import java.util.List;

public class PostResponseDTO {
    public Integer postId;
    public String caption;
    public String imageUrl;
    public String createdAt;
    public Integer likeCount;
    public Integer commentCount;
    public Integer shareCount;
    public Boolean likedByMe;
    public List<String> styles;

    public List<CommentResponseDTO> comments; // ✅ new

    public PostResponseDTO(Post post, Integer currentUserId) {
        this.postId = post.getId();
        this.caption = post.getCaption();
        this.imageUrl = post.getImageUrl();
        this.createdAt = post.getCreatedAt().toString();
        this.likeCount = post.getLikes().size();
        this.commentCount = post.getComments().size();
        this.shareCount = post.getShares().size();
        this.likedByMe = currentUserId != null &&
                post.getLikes().stream().anyMatch(like -> like.getUser().getUserId().equals(currentUserId));
        this.styles = List.of(); // ใส่ภายหลังจาก controller

        this.comments = post.getComments().stream()
                .map(CommentResponseDTO::new)
                .toList();
    }
}

