package com.example.springservice.controller;

import com.example.springservice.SessionUtil;
import com.example.springservice.entites.*;
import com.example.springservice.entites.enmap.Commission;
import com.example.springservice.repo.*;
import com.example.springservice.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
// ✅ ReportController.java

@RestController
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private CommissionRepository commissionRepository;

    @PostMapping
    public ResponseEntity<?> createReport(@RequestBody ReportCreateDTO dto, HttpServletRequest request) {
        User reporter = SessionUtil.requireSessionUser(userRepository, request);
        System.out.println("reported by : " + reporter);
        if (reporter.getUserId().equals(dto.reportedUserId)) {
            return ResponseEntity.badRequest().body(Map.of("error", "You cannot report yourself"));
        }

        Optional<User> reportedUserOpt = userRepository.findById(dto.reportedUserId);
        if (reportedUserOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Reported user not found"));
        }

        Commission commission = null;
        if (dto.commissionId != null) {
            commission = commissionRepository.findById(dto.commissionId)
                    .orElse(null);
            if (commission == null) {
                return ResponseEntity.status(404).body(Map.of("error", "Commission not found"));
            }
        }

        Report report = new Report();
        report.setReporter(reporter);
        report.setReportedUser(reportedUserOpt.get());
        report.setCommission(commission);
        report.setDescription(dto.description);
        report.setReportType(Report.ReportType.valueOf(dto.reportType.toUpperCase()));
        report.setStatus(Report.ReportStatus.PENDING);
        report.setCreatedAt(LocalDateTime.now());

        reportRepository.save(report);
        return ResponseEntity.ok(Map.of("message", "Report submitted"));
    }

}
