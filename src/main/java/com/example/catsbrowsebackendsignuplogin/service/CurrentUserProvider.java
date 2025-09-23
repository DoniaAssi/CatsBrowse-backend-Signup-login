package com.example.catsbrowsebackendsignuplogin.service;


import com.example.catsbrowsebackendsignuplogin.entity.AppUser;
import com.example.catsbrowsebackendsignuplogin.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserProvider {
    private final UserRepository users;
    public CurrentUserProvider(UserRepository users){ this.users = users; }

    public AppUser getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return users.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }
}
