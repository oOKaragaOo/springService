//package com.example.springservice.controller;
//
//import com.example.springservice.SessionUtil;
//import com.example.springservice.entites.*;
//import com.example.springservice.repo.*;
//import com.example.springservice.dto.*;
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//// ✅ ReportController.java
//
//@RestController
//@RequestMapping("/reports")
//public class ReportController {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private ReportRepository reportRepository;
//
//    @PostMapping
//    public ResponseEntity<?> createReport(@RequestBody ReportCreateDTO dto, HttpServletRequest request) {
//        User reporter = SessionUtil.requireSessionUser(userRepository, request);
//        if (reporter.getUserId().equals(dto.reportedUserId)) {
//            return ResponseEntity.badRequest().body(Map.of("error", "You cannot report yourself"));
//        }
//
//        Report report = new Report();
//        report.setReporter(reporter.getUserId());
//        report.setReportedUser(new User(dto.reportedUserId));
//        report.setCommissionId(dto.commissionId);
//        report.setReportType(dto.reportType);
//        report.setDescription(dto.description);
//        report.setStatus(Report.ReportStatus.pending);
//        report.setCreatedAt(LocalDateTime.now());
//
//        reportRepository.save(report);
//        return ResponseEntity.ok(Map.of("message", "Report submitted"));
//    }
//
//    @GetMapping("/my")
//    public ResponseEntity<?> getMyReports(HttpServletRequest request) {
//        User user = SessionUtil.requireSessionUser(userRepository, request);
//        List<Report> reports = reportRepository.findAllByReporter_UserId(user.getUserId());
//        return ResponseEntity.ok(reports);
//    }
//
//    // 🔒 Admin: View all reports
//    @GetMapping
//    public ResponseEntity<?> getAllReports(HttpServletRequest request) {
//        User admin = SessionUtil.requireSessionUser(userRepository, request);
//        if (!admin.getRole().equals("admin")) {
//            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
//        }
//        return ResponseEntity.ok(reportRepository.findAll());
//    }
//
//    @PostMapping("/{id}/resolve")
//    public ResponseEntity<?> resolveReport(@PathVariable Integer id, HttpServletRequest request) {
//        User admin = SessionUtil.requireSessionUser(userRepository, request);
//        if (!admin.getRole().equals("admin")) {
//            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
//        }
//
//        Optional<Report> reportOpt = reportRepository.findById(id);
//        if (reportOpt.isEmpty()) return ResponseEntity.status(404).body(Map.of("error", "Report not found"));
//
//        Report report = reportOpt.get();
//        report.setStatus(Report.ReportStatus.resolved);
//        report.setResolvedByAdmin(admin);
//        report.setResolvedAt(LocalDateTime.now());
//
//        reportRepository.save(report);
//        return ResponseEntity.ok(Map.of("message", "Report marked as resolved"));
//    }
//}
