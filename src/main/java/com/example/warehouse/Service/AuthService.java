package com.example.warehouse.Service;

import com.example.warehouse.Dto.Request.LoginRequest;
import com.example.warehouse.Dto.Request.RegisterRequest;
import com.example.warehouse.Dto.Response.AuthResponse;
import com.example.warehouse.Entity.User;
import com.example.warehouse.Enum.Role;
import com.example.warehouse.Repository.UserRepository;
import com.example.warehouse.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username đã tồn tại");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Chuyển role string sang enum
        if ("ADMIN".equalsIgnoreCase(request.getRole())) {
            user.setRole(Role.ADMIN);
        } else {
            user.setRole(Role.NHANVIEN);
        }

        userRepository.save(user);

        // Sinh token ngay khi đăng ký
        return jwtTokenProvider.generateToken(user);
    }
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Username không tồn tại"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Password không đúng");
        }

        String token = jwtTokenProvider.generateToken(user);
        return new AuthResponse(token, user.getRole().name());
    }
}

