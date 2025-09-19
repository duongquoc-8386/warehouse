package com.example.warehouse.Config;

import com.example.warehouse.security.JwtTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;
    private final UserDetailsService userDetailsService;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session
                        -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                                // Cho phép swagger
                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                                // Cho phép register, login, excel
                                .requestMatchers("/api/warehouse/register", "/api/warehouse/login", "/api/warehouse/excel/**").permitAll()
                                // Sản phẩm: chỉ nhân viên + admin
                                .requestMatchers("/api/warehouse/products/**").hasAnyRole("NHANVIEN", "ADMIN")
                                // Báo cáo: cho phép truy cập tự do
                                .requestMatchers("/api/reports/**").permitAll()
                                .requestMatchers("/api/warehouse/**").permitAll()
                                // Admin
                                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                                // Nhân viên
                                .requestMatchers("/api/nhanvien/**").hasAnyRole("NHANVIEN","ADMIN")
                        // Các API còn lại thì bắt buộc authenticated
                )

                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

}