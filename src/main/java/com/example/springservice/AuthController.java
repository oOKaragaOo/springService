package com.example.springservice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        User user = authService.register(request.get("name"), request.get("email"), request.get("password"));
        System.out.println("############---- User registered successfully");
        return ResponseEntity.ok(Map.of("message", "User registered", "userId", user.getId()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        System.out.println("############---- User login successfully");
        return authService.authenticate(request.get("email"), request.get("password"))
                .map(user -> ResponseEntity.ok(Map.of("message", "Login successful", "userId", user.getId())))

                .orElse(ResponseEntity.status(401).body(Map.of("message", "Invalid credentials")));
    }
}
