package com.example.warehouse.Controller;

import com.example.warehouse.Dto.Request.RegisterRequest;
import com.example.warehouse.Dto.Request.LoginRequest;
import com.example.warehouse.Dto.Response.AuthResponse;
import com.example.warehouse.Service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/warehouse")
public class AuthController {

    @Autowired
    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        String token = authService.register(request); // trả về token chứa role
        return ResponseEntity.ok().body(token);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request); // gọi service login
        return ResponseEntity.ok(response);
    }
}
