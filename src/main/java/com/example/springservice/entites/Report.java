//package com.example.springservice.entites;
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.Setter;
//
//import java.time.LocalDateTime;
//@Getter
//@Setter
//
//@Entity
//@Table(name = "Report")
//public class Report {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "report_id")
//    private int reportId;
//
//    @Column(name = "reporter_id", nullable = false)
//    private int reporter;
//
//    @Column(name = "reported_user_id", nullable = false)
//    private int reportedUser;
//
//    @Column(name = "commission_id", nullable = true)
//    private int commissionId;
//
//    @Enumerated(EnumType.STRING)
//    @Column(name = "report_type", nullable = false)
//    private String reportType;
//
//    @Column(name = "description", columnDefinition = "TEXT")
//    private String description;
//
//    @Enumerated(EnumType.STRING)
//    @Column(name = "status", nullable = false)
//    private String status;
//
//    @Column(name = "created_at", nullable = false, updatable = false)
//    private LocalDateTime createdAt;
//
//    @Column(name = "resolved_by_admin_id")
//    private int resolvedByAdminId;
//
//    @Column(name = "resolved_at")
//    private LocalDateTime resolvedAt;
//}