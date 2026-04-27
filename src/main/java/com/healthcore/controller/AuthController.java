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
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.of(authService.login(request));
    }

    @GetMapping("/me")
    public ApiResponse<AuthResponse> getMe() {
        return ApiResponse.of(authService.getMe());
    }
}
