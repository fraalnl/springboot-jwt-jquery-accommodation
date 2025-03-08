package edu.tus.controller;

import edu.tus.dto.LoginRequest;
import edu.tus.security.JwtUserDetailsService;
import edu.tus.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtUserDetailsService jwtUserDetailsService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthController(JwtUserDetailsService jwtUserDetailsService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {
        try {
            UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(loginRequest.getUsername());
            if (passwordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword())) {
                // if the user has ROLE_ADMIN, assign admin; otherwise, student.
                String role = "ROLE_STUDENT";
                if (userDetails.getAuthorities().stream()
                        .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
                    role = "ROLE_ADMIN";
                }
                String token = jwtUtil.generateToken(userDetails.getUsername(), role);
                return ResponseEntity.ok(Map.of("token", token));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid credentials. Please try again."));
            }
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials. Please try again."));
        }
    }
}
