package com.example.catsbrowsebackendsignuplogin.controller;

import com.example.catsbrowsebackendsignuplogin.dto.AuthResponse;
import com.example.catsbrowsebackendsignuplogin.dto.LoginRequest;
import com.example.catsbrowsebackendsignuplogin.dto.SignupRequest;
import com.example.catsbrowsebackendsignuplogin.entity.AppUser;
import com.example.catsbrowsebackendsignuplogin.security.JwtService;
import com.example.catsbrowsebackendsignuplogin.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService users;
    private final JwtService jwt;

    public AuthController(UserService users, JwtService jwt) {
        this.users = users;
        this.jwt = jwt;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody SignupRequest req) {
        AppUser u = users.signup(req);
        String token = jwt.generate(u.getEmail());

        AuthResponse resp = new AuthResponse(
                token,
                u.getId(),
                u.getEmail(),
                String.join(",", u.getRoles().stream().map(Enum::name).toList())
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            AppUser u = users.validateLogin(req);
            String token = jwt.generate(u.getEmail());

            AuthResponse resp = new AuthResponse(
                    token,
                    u.getId(),
                    u.getEmail(),
                    String.join(",", u.getRoles().stream().map(Enum::name).toList())
            );

            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorMessage("Wrong email or password"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal User principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = principal.getUsername();
        String rolesCsv = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        String mainRole = rolesCsv.contains("ROLE_ADMIN") ? "ROLE_ADMIN"
                : (rolesCsv.contains("ROLE_USER") ? "ROLE_USER" : rolesCsv);

        return ResponseEntity.ok(new MeResponse(email, mainRole, rolesCsv));
    }

    public record ErrorMessage(String message) {}
    public record MeResponse(String email, String role, String roles) {}
}

