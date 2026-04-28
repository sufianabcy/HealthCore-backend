package com.healthcore.controller;

import com.healthcore.dto.request.LoginRequest;
import com.healthcore.dto.response.ApiResponse;
import com.healthcore.dto.response.AuthResponse;
import com.healthcore.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public org.springframework.http.ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            return org.springframework.http.ResponseEntity.ok(ApiResponse.of(authService.login(request)));
        } catch (Exception e) {
            e.printStackTrace(); // 🔥 ADDED THIS TO CATCH SILENT 500s
            return org.springframework.http.ResponseEntity.status(500).body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ApiResponse<AuthResponse> getMe() {
        return ApiResponse.of(authService.getMe());
    }
}
