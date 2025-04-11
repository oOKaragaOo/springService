package com.example.springservice;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<?> register(@RequestBody Map<String, String> request, HttpServletRequest httpRequest) {
        String name = request.get("name");
        String email = request.get("email");
        String password = request.get("password");
        String role = request.get("role");

        if (name == null || email == null || password == null || role == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Missing required fields"));
        }

        if (authService.userExists(email)) {
            return ResponseEntity.status(409).body(Map.of("message", "Email already registered"));
        }

        User user = authService.register(name, email, password);

        // ✅ เรียกใช้ helper ที่แยกไว้
        SessionUtil.storeUserSession(httpRequest, user);

        return ResponseEntity.ok(Map.of("message", "User registered", "userId", user.getId()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request, HttpServletRequest httpRequest) {
        System.out.println("----> 🟢 POST /login called");

        return authService.authenticate(request.get("email"), request.get("password"))
                .map(user -> {
                    SessionUtil.storeUserSession(httpRequest, user); // ✅ ใช้ helper ที่แยกไว้
                    return ResponseEntity.ok(Map.of(
                            "message", "Login successful",
                            "userId", user.getId()
                    ));
                })
                .orElse(ResponseEntity.status(401).body(Map.of("message", "Invalid credentials")));
    }


    @GetMapping("/session")
    public ResponseEntity<?> session(HttpServletRequest request) {
        System.out.println("----> 🟢 GET /auth/session called");

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            return ResponseEntity.ok(Map.of("user", Map.of(
                    "name", user.getName(),
                    "email", user.getEmail()
            )));
        }
        return ResponseEntity.status(401).body(Map.of("message", "Not logged in"));
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        System.out.println("----> 🟢 POST /auth/logout called");
        SessionUtil.clearUserSession(request);
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }
}


class SessionUtil {
    public static void storeUserSession(HttpServletRequest request, User user) {

        HttpSession session = request.getSession(true); // true = สร้างใหม่ถ้าไม่มี
        session.setAttribute("user", user);
    }

    public static void clearUserSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
