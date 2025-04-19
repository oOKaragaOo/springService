package com.example.springservice.controller;

import com.example.springservice.dto.PostResponseDTO;
import com.example.springservice.dto.UserPublicDTO;
import com.example.springservice.entites.Post;
import com.example.springservice.entites.User;
import com.example.springservice.entites.enmap.ArtistStyle;
import com.example.springservice.repo.*;
import com.example.springservice.repo.PostRepository;
import com.example.springservice.repo.UserFollowsRepository;
import com.example.springservice.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

// ✅ GuestController.java
@RestController
@RequestMapping("/guest")
public class GuestController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserFollowsRepository userFollowsRepository;

    @Autowired
    private ArtistStyleRepository artistStyleRepository;

//    @Autowired
//    private ArtistStyleMappingRepository artistStyleMappingRepository;

    // 1. Get All Users
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userRepository.findAllSortedByName();
        List<UserPublicDTO> response = users.stream().map(UserPublicDTO::new).toList();
        return ResponseEntity.ok(response);
    }

    // 2. Search Users by Name
    @GetMapping("/users/search")
    public ResponseEntity<?> searchUsersByName(@RequestParam String name) {
        List<User> users = userRepository.searchByNameSorted(name);
        List<UserPublicDTO> response = users.stream().map(UserPublicDTO::new).toList();
        return ResponseEntity.ok(response);
    }

    // 3. Get All Posts (with style tags)
//    @GetMapping("/posts")
//    public ResponseEntity<?> getAllPosts() {
//        List<Post> posts = postRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
//        List<PostResponseDTO> response = posts.stream()
//                .map(post -> {
//                    List<String> styles = artistStyleRepository
//                            .findAllById(post.getAuthor().getUserId())
//                            .stream()
//                            .map(mapping -> mapping.getStyle().getStyleName())
//                            .toList();
//                    PostResponseDTO dto = new PostResponseDTO(post, null);
//                    dto.styles = styles;
//                    return dto;
//                })
//                .toList();
//        return ResponseEntity.ok(response);
//    }
//
//    // 4. Get Post by ID
//    @GetMapping("/posts/{id}")
//    public ResponseEntity<?> getPostById(@PathVariable Integer id) {
//        Optional<Post> postOpt = postRepository.findById(id);
//        if (postOpt.isEmpty()) return ResponseEntity.status(404).body(Map.of("error", "Post not found"));
//
//        PostResponseDTO dto = new PostResponseDTO(postOpt.get(), null);
//        List<String> styles = artistStyleRepository
//                .findAllByUserId(postOpt.get().getAuthor().getUserId())
//                .stream()
//                .map(m -> m.getStyle().getStyleName())
//                .toList();
//        dto.styles = styles;
//
//        return ResponseEntity.ok(dto);
//    }

    // 5. Get All Styles
    @GetMapping("/styles")
    public ResponseEntity<?> getAllStyles() {
        List<ArtistStyle> styles = artistStyleRepository.findAll(Sort.by("styleName"));
        List<String> response = styles.stream().map(ArtistStyle::getStyleName).toList();
        return ResponseEntity.ok(response);
    }
}

