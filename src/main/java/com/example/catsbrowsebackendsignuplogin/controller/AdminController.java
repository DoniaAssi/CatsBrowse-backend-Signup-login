package com.example.catsbrowsebackendsignuplogin.controller;

import com.example.catsbrowsebackendsignuplogin.dto.UpdateUserRequest;
import com.example.catsbrowsebackendsignuplogin.entity.AppUser;
import com.example.catsbrowsebackendsignuplogin.entity.Favorite;
import com.example.catsbrowsebackendsignuplogin.repository.FavoriteRepository;
import com.example.catsbrowsebackendsignuplogin.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@CrossOrigin
public class AdminController {

    private final FavoriteRepository favRepo;
    private final UserService userService;

    public AdminController(FavoriteRepository favRepo, UserService userService) {
        this.favRepo = favRepo;
        this.userService = userService;
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Map<String,Object>> listUsers() {
        return userService.findAll().stream()
                .map(u -> Map.of(
                        "id", u.getId(),
                        "email", u.getEmail(),
                        "roles", u.getRoles().stream().map(Enum::name).collect(Collectors.toList())
                ))
                .toList();
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest req) {
        Optional<AppUser> opt = userService.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        AppUser u = opt.get();

        if (req.getEmail() != null && !req.getEmail().isBlank())
            u.setEmail(req.getEmail().trim());

        if (req.getPassword() != null && !req.getPassword().isBlank())
            userService.changePassword(u, req.getPassword());

        if (req.getRolesCsv() != null) {
            Set<String> norm = Arrays.stream(req.getRolesCsv().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(s -> s.startsWith("ROLE_") ? s.substring(5) : s)
                    .map(String::toUpperCase)
                    .collect(Collectors.toSet());
            userService.setRolesByNames(u, norm);
        }

        userService.save(u);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.findById(id).isEmpty()) return ResponseEntity.notFound().build();
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/favorite/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAnyFavorite(@PathVariable Long id) {
        if (!favRepo.existsById(id)) return ResponseEntity.notFound().build();
        favRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/favorite/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateFavorite(@PathVariable Long id, @RequestBody Favorite body) {
        return favRepo.findById(id)
                .map(f -> {
                    if (body.getCatImage() != null) f.setCatImage(body.getCatImage());
                    if (body.getCatId() != null) f.setCatId(body.getCatId());
                    favRepo.save(f);
                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}