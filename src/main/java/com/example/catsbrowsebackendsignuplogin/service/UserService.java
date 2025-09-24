package com.example.catsbrowsebackendsignuplogin.service;


import com.example.catsbrowsebackendsignuplogin.dto.LoginRequest;
import com.example.catsbrowsebackendsignuplogin.dto.SignupRequest;
import com.example.catsbrowsebackendsignuplogin.entity.AppUser;
import com.example.catsbrowsebackendsignuplogin.entity.Role;
import com.example.catsbrowsebackendsignuplogin.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;

    public UserService(UserRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    public AppUser signup(SignupRequest req) {
        String email = req.getEmail();
        String password = req.getPassword();

        if (email == null || password == null || email.isBlank() || password.isBlank()) {
            throw new IllegalArgumentException("Email and password are required");
        }
        if (repo.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        AppUser u = new AppUser();
        u.setEmail(email.trim());
        u.setPassword(encoder.encode(password));
        u.setRoles(Set.of(Role.USER));

        return repo.save(u);
    }

    public AppUser validateLogin(LoginRequest req) {
        String email = req.getEmail();
        String password = req.getPassword();

        AppUser u = repo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Wrong email or password"));

        if (!encoder.matches(password, u.getPassword())) {
            throw new IllegalArgumentException("Wrong email or password");
        }
        return u;
    }

    public List<AppUser> findAll() {
        return repo.findAll();
    }

    public Optional<AppUser> findById(Long id) {
        return repo.findById(id);
    }

    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    public void save(AppUser user) {
        repo.save(user);
    }

    public void changePassword(AppUser user, String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) return;
        user.setPassword(encoder.encode(rawPassword));
    }

    public void setRolesByNames(AppUser user, Set<String> roleNames) {
        if (roleNames == null) return;

        Set<Role> roles = roleNames.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> s.toUpperCase(Locale.ROOT))
                .map(s -> s.startsWith("ROLE_") ? s.substring(5) : s)
                .map(Role::valueOf)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(Role.class)));

        if (!roles.isEmpty()) {
            user.setRoles(roles);
        }
    }
}