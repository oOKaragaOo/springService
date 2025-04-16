package com.example.springservice.controller;

import com.example.springservice.*;
import com.example.springservice.dto.UserProfileDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

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
        Integer userId = SessionUtil.getUserIdFromSession(request);
        if (userId == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return ResponseEntity.status(404).body(Map.of("error", "User not found"));

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

}
