// ✅ AdminController.java
package com.example.springservice.controller;

import com.example.springservice.*;
import com.example.springservice.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    private boolean isAdmin(User user) {
        return user.getRole().equalsIgnoreCase("admin");
    }

    private ResponseEntity<?> checkAdminAccess(HttpServletRequest request) {
        Integer requesterId = SessionUtil.getUserIdFromSession(request);
        if (requesterId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        User admin = userRepository.findById(requesterId).orElse(null);
        if (admin == null || !isAdmin(admin)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }

        return ResponseEntity.ok(admin);
    }

    @PatchMapping("/ban-user/{id}")
    public ResponseEntity<?> banUser(@PathVariable Integer id, HttpServletRequest request) {
        ResponseEntity<?> accessCheck = checkAdminAccess(request);
        if (!accessCheck.getStatusCode().is2xxSuccessful()) return accessCheck;

        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) return ResponseEntity.status(404).body(Map.of("error", "User not found"));

        User targetUser = userOpt.get();
        targetUser.setStatus(User.Status.banned);
        userRepository.save(targetUser);

        return ResponseEntity.ok(Map.of("message", "User has been banned"));
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(HttpServletRequest request) {
        ResponseEntity<?> accessCheck = checkAdminAccess(request);
        if (!accessCheck.getStatusCode().is2xxSuccessful()) return accessCheck;

        List<UserListDTO> users = userRepository.findAll().stream()
                .map(UserListDTO::new)
                .toList();

        return ResponseEntity.ok(users);
    }
}