package com.example.springservice.dto;

import com.example.springservice.entites.Comment;
import com.example.springservice.entites.Post;

import java.time.LocalDateTime;
import java.util.List;

public class PostResponseDTO {
    public Integer id;
    public String caption;
    public String imageUrl;
    public String authorName;
    public Integer likeCount;
    public Integer commentCount;
    public LocalDateTime createdAt;
    public Boolean likedByMe;
    public List<String> comments;
    public List<String> styles; // ✅ tag style

    public PostResponseDTO(Post post, Integer currentUserId) {
        this.id = post.getId();
        this.caption = post.getCaption();
        this.imageUrl = post.getImageUrl();
        this.authorName = post.getAuthor().getName();
        this.likeCount = post.getLikes().size();
        this.commentCount = post.getComments().size();
        this.createdAt = post.getCreatedAt();
        this.likedByMe = currentUserId != null && post.getLikes().stream()
                .anyMatch(like -> like.getUser().getUserId().equals(currentUserId));
        this.comments = post.getComments().stream()
                .map(Comment::getContent)
                .toList();
    }
}
