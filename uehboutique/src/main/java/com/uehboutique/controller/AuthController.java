package com.uehboutique.controller;

import com.uehboutique.dto.request.LoginRequest;
import com.uehboutique.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    // API đăng nhập
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            return ResponseEntity.ok(authService.login(request.getUsername(), request.getPassword()));
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    // API đăng xuất
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(authService.logout());
    }
}
