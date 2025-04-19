package com.example.springservice.controller;

import com.example.springservice.*;
import com.example.springservice.dto.*;
import com.example.springservice.entites.*;
import com.example.springservice.repo.*;
import com.example.springservice.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
        System.out.println("----> 🟢 GET /user/profile called");
        User sessionUser = (User) request.getSession().getAttribute("user");
        if (sessionUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        return ResponseEntity.ok(Map.of("user", new UserProfileDTO(sessionUser)));
    }

    @GetMapping("/posts/{userId}") // for profile
    public ResponseEntity<?> getPostsByUserId(@PathVariable Integer userId) {
        List<Post> posts = postRepository.findAllByAuthor_UserIdOrderByCreatedAtDesc(userId);
        List<PostResponseDTO> response = posts.stream()
                .map(post -> new PostResponseDTO(post, userId))
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchUsersByName(@RequestParam String name, HttpServletRequest request) {
        User currentUser = SessionUtil.requireSessionUser(userRepository, request);
        System.out.println("----> searchUsersByName called");
        List <User> users = userRepository.findByNameContainingIgnoreCase(name);
        List<UserProfileDTO> response = users.stream()
                .map(user -> new UserProfileDTO(user, currentUser.getUserId(), userFollowsRepository))
                .toList();

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

        User following = followingOpt.get();

        UserFollows follow = new UserFollows();
        follow.setFollower(follower); // auto set followerId in FollowId
        follow.setFollowing(following); // auto set followingId in FollowId
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
        User user = SessionUtil.requireSessionUser(userRepository, request);

        List<UserFollows> following = userFollowsRepository.findAllByFollower_UserId(user.getUserId());
        List<String> usernames = following.stream().map(f -> f.getFollowing().getName()).toList();

        return ResponseEntity.ok(Map.of("following", usernames));
    }

    @GetMapping("/followers")
    public ResponseEntity<?> getFollowers(HttpServletRequest request) {
        User user = SessionUtil.requireSessionUser(userRepository, request);

        List<UserFollows> followers = userFollowsRepository.findAllByFollowing_UserId(user.getUserId());
        List<String> usernames = followers.stream().map(f -> f.getFollower().getName()).toList();

        return ResponseEntity.ok(Map.of("followers", usernames));
    }

    @GetMapping("/{id}/followers")
    public ResponseEntity<?> getFollowersOfUser(@PathVariable Integer id, HttpServletRequest request) {
        User currentUser = SessionUtil.requireSessionUser(userRepository, request);
        if (!currentUser.getRole().equals("admin") && !currentUser.getUserId().equals(id)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }

        List<UserFollows> followers = userFollowsRepository.findAllByFollowing_UserId(id);
        List<UserProfileDTO> response = followers.stream()
                .map(f -> new UserProfileDTO(f.getFollower(), currentUser.getUserId(), userFollowsRepository))
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/following")
    public ResponseEntity<?> getFollowingOfUser(@PathVariable Integer id, HttpServletRequest request) {
        User currentUser = SessionUtil.requireSessionUser(userRepository, request);
        if (!currentUser.getRole().equals("admin") && !currentUser.getUserId().equals(id)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }

        List<UserFollows> following = userFollowsRepository.findAllByFollower_UserId(id);
        List<UserProfileDTO> response = following.stream()
                .map(f -> new UserProfileDTO(f.getFollowing(), currentUser.getUserId(), userFollowsRepository))
                .toList();
        return ResponseEntity.ok(response);
    }

}

@RestController
@RequestMapping("/guest")
class GuestController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserFollowsRepository userFollowsRepository;

    // 🔹 1. Get All Users (guest view)
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userRepository.findAllSortedByName();
        List<UserPublicDTO> response = users.stream().map(UserPublicDTO::new).toList();
        return ResponseEntity.ok(response);
    }

    // 🔹 2. Search Users by Name (guest view)
    @GetMapping("/users/search")
    public ResponseEntity<?> searchUsersByName(@RequestParam String name) {
        List<User> users = userRepository.searchByNameSorted(name);
        List<UserPublicDTO> response = users.stream().map(UserPublicDTO::new).toList();
        return ResponseEntity.ok(response);
    }

    // 🔹 3. Get All Posts (guest feed)
    @GetMapping("/posts")
    public ResponseEntity<?> getAllPosts() {
        List<Post> posts = postRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        List<PostResponseDTO> response = posts.stream()
                .map(post -> new PostResponseDTO(post, null))
                .toList();
        return ResponseEntity.ok(response);
    }

    // 🔹 4. Get Post by ID (guest view)
    @GetMapping("/posts/{id}")
    public ResponseEntity<?> getPostById(@PathVariable Integer id) {
        Optional<Post> postOpt = postRepository.findById(id);
        if (postOpt.isEmpty()) return ResponseEntity.status(404).body(Map.of("error", "Post not found"));
        return ResponseEntity.ok(new PostResponseDTO(postOpt.get(), null));
    }
}
