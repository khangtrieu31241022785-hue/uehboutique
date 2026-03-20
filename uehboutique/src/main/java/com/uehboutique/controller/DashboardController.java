package com.uehboutique.controller;

import com.uehboutique.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    // API lấy toàn bộ thông tin thống kê
    // Cách gọi: GET http://localhost:8080/api/dashboard
    @GetMapping
    public ResponseEntity<?> getDashboardStats() {
        try {
            return ResponseEntity.ok(dashboardService.getDashboardStats());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error retrieving statistics data: " + e.getMessage());
        }
    }
}