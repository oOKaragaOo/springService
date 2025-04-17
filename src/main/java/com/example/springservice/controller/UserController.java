package com.example.springservice.controller;

import com.example.springservice.*;
import com.example.springservice.dto.PostResponseDTO;
import com.example.springservice.dto.UserProfileDTO;
import com.example.springservice.entites.*;
import com.example.springservice.entites.User;
import com.example.springservice.entites.UserFollows;
import com.example.springservice.repo.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthService authService;
    @Autowired
    private UserFollowsRepository userFollowsRepository;

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody User updatedUser, HttpServletRequest request) {
        System.out.println("----> 🟡 PUT /auth/profile called");

        Integer userId = SessionUtil.getUserIdFromSession(request);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        }

        User existingUser = userOpt.get();
        // field update
        existingUser.setName(updatedUser.getName());
        existingUser.setProfile_picture(updatedUser.getProfile_picture());
        existingUser.setDescription(updatedUser.getDescription());
        existingUser.setCommission_status(updatedUser.getCommission_status());

        userRepository.save(existingUser);
        // ✅ Return DTO
        return ResponseEntity.ok(Map.of(
                "message", "Profile updated successfully",
                "user", new UserProfileDTO(existingUser)
        ));
    }

    @PatchMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> body, HttpServletRequest request) {
        String oldPass = body.get("oldPassword");
        String newPass = body.get("newPassword");

        Integer userId = SessionUtil.getUserIdFromSession(request);
        if (userId == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) return ResponseEntity.status(404).body(Map.of("error", "User not found"));

        User user = userOpt.get();
        if (!authService.matchesPassword(oldPass, user.getPassword())) return ResponseEntity.badRequest().body(Map.of("error", "Incorrect old password"));

        user.setPassword(authService.encodePassword(newPass));
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }

    @PatchMapping("/change-email")
    public ResponseEntity<?> changeEmail(@RequestBody Map<String, String> body, HttpServletRequest request) {
        String newEmail = body.get("email");

        Integer userId = SessionUtil.getUserIdFromSession(request);
        if (userId == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

        if (userRepository.findByEmail(newEmail).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email already in use"));
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return ResponseEntity.status(404).body(Map.of("error", "User not found"));

        user.setEmail(newEmail);
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Email updated successfully"));
    }

    @PostMapping("/refresh-session")
    public ResponseEntity<?> refreshSession(HttpServletRequest request) {
        User user = SessionUtil.requireSessionUser(userRepository, request);
        SessionUtil.storeUserSession(request, user);
        return ResponseEntity.ok(Map.of("message", "Session refreshed", "user", new UserProfileDTO(user)));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(HttpServletRequest request) {
        User sessionUser = (User) request.getSession().getAttribute("user");
        if (sessionUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        return ResponseEntity.ok(Map.of("user", new UserProfileDTO(sessionUser)));
    }

    @GetMapping("/posts/{userId}")
    public ResponseEntity<?> getPostsByUserId(@PathVariable Integer userId) {
        List<Post> posts = postRepository.findAllByAuthor_UserIdOrderByCreatedAtDesc(userId);
        List<PostResponseDTO> response = posts.stream().map(PostResponseDTO::new).toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/follow")
    public ResponseEntity<?> followUser(@PathVariable Integer id, HttpServletRequest request) {
        User follower = SessionUtil.requireSessionUser(userRepository, request);
        if (follower.getUserId().equals(id)) {
            return ResponseEntity.badRequest().body(Map.of("error", "You cannot follow yourself"));
        }

        Optional<User> followingOpt = userRepository.findById(id);
        if (followingOpt.isEmpty()) return ResponseEntity.status(404).body(Map.of("error", "User not found"));

        FollowId followId = new FollowId();
        followId.setFollowerId(follower.getUserId());
        followId.setFollowingId(id);

        if (userFollowsRepository.existsById(followId)) {
            return ResponseEntity.ok(Map.of("message", "Already following"));
        }

        UserFollows follow = new UserFollows();
        follow.setId(followId);
        follow.setFollower(follower);
        follow.setFollowing(followingOpt.get());
        follow.setFollowDate(LocalDateTime.now());

        userFollowsRepository.save(follow);
        return ResponseEntity.ok(Map.of("message", "Followed successfully"));
    }

    @DeleteMapping("/{id}/unfollow")
    public ResponseEntity<?> unfollowUser(@PathVariable Integer id, HttpServletRequest request) {
        User follower = SessionUtil.requireSessionUser(userRepository, request);

        FollowId followId = new FollowId();
        followId.setFollowerId(follower.getUserId());
        followId.setFollowingId(id);

        if (!userFollowsRepository.existsById(followId)) {
            return ResponseEntity.badRequest().body(Map.of("error", "You are not following this user"));
        }

        userFollowsRepository.deleteById(followId);
        return ResponseEntity.ok(Map.of("message", "Unfollowed successfully"));
    }

    @GetMapping("/following")
    public ResponseEntity<?> getFollowing(HttpServletRequest request) {
        User user = SessionUtil.requireSessionUser(userRepository, request); // ✅ แทน 3 บรรทัด

        List<UserFollows> following = userFollowsRepository.findAllByFollower_UserId(user.getUserId());
        List<String> usernames = following.stream().map(f -> f.getFollowing().getName()).toList();

        return ResponseEntity.ok(Map.of("following", usernames));
    }

    @GetMapping("/followers")
    public ResponseEntity<?> getFollowers(HttpServletRequest request) {
        User user = SessionUtil.requireSessionUser(userRepository, request); // ✅ แทน 3 บรรทัด

        List<UserFollows> followers = userFollowsRepository.findAllByFollowing_UserId(user.getUserId());
        List<String> usernames = followers.stream().map(f -> f.getFollower().getName()).toList();

        return ResponseEntity.ok(Map.of("followers", usernames));
    }


}
