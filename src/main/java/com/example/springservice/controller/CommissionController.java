package com.example.springservice.controller;

import com.example.springservice.SessionUtil;
import com.example.springservice.dto.*;
import com.example.springservice.entites.*;
import com.example.springservice.entites.enmap.Commission;
import com.example.springservice.repo.*;
import com.example.springservice.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

// 📁 CommissionController.java
@RestController
@RequestMapping("/commissions")
public class CommissionController {

    @Autowired
    private CommissionRepository commissionRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private NotificationService notificationService;

    @PostMapping
    public ResponseEntity<?> createCommission(@RequestBody CommissionCreateDTO dto, HttpServletRequest request) {
        User customer = SessionUtil.requireSessionUser(userRepository, request);
        System.out.println("--> createCommission");
        if (dto.artistId == null || dto.title == null || dto.price == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing required fields"));
        }

        // ตรวจสอบว่า User กำลังสร้าง Commission ให้ตัวเองหรือไม่
        if (dto.artistId.equals(customer.getUserId())) {
            return ResponseEntity.badRequest().body(Map.of("error", "User cannot create a commission for themselves"));
        }

        Optional<User> artistOpt = userRepository.findById(dto.artistId);
        if (artistOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Artist not found"));
        }

        Commission commission = new Commission();
        commission.setCustomer(customer);
        commission.setArtist(artistOpt.get());
        commission.setTitle(dto.title);
        commission.setDescription(dto.description);
        commission.setPrice(dto.price);
        commission.setDeadline(dto.deadline);
        commission.setStatus(Commission.Status.REQUESTED);
        commissionRepository.save(commission);

        notificationService.sendNotiTo(artistOpt.get(), "NEW_COMMISSION", "📥 มีคำขอใหม่จาก " + customer.getName());

        return ResponseEntity.ok(Map.of("message", "Commission created", "id", commission.getCommissionId()));
    }
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Integer id, @RequestParam String status, HttpServletRequest request) {
        User user = SessionUtil.requireSessionUser(userRepository, request);
        Optional<Commission> comOpt = commissionRepository.findById(id);
        if (comOpt.isEmpty()) return ResponseEntity.status(404).body(Map.of("error", "Not found"));

        Commission com = comOpt.get();
        if (!com.getArtist().getUserId().equals(user.getUserId())) {
            return ResponseEntity.status(403).body(Map.of("error", "Only artist can change status"));
        }

        try {
            com.setStatus(Commission.Status.valueOf(status));
            commissionRepository.save(com);
            notificationService.sendNotiTo(com.getCustomer(), "COMMISSION_UPDATED", "📌 คำขอของคุณถูกอัปเดตเป็น: " + status);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid status"));
        }

        return ResponseEntity.ok(Map.of("message", "Status updated"));
    }

    @GetMapping
    public ResponseEntity<?> getMyCommissions(HttpServletRequest request) {
        User user = SessionUtil.requireSessionUser(userRepository, request);
        List<Commission> list;
        if ("artist".equalsIgnoreCase(user.getRole())) {
            list = commissionRepository.findAllByArtistOrderByCreatedAtDesc(user);
        } else {
            list = commissionRepository.findAllByCustomerOrderByCreatedAtDesc(user);
        }
        return ResponseEntity.ok(Map.of("commissions", list));
    }
}
