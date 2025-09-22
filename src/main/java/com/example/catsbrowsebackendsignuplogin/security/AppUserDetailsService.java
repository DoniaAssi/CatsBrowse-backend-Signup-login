package com.example.catsbrowsebackendsignuplogin.security;

import com.example.catsbrowsebackendsignuplogin.entity.AppUser;
import com.example.catsbrowsebackendsignuplogin.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class AppUserDetailsService implements UserDetailsService {
    private final UserRepository repo;
    public AppUserDetailsService(UserRepository repo) { this.repo = repo; }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser u = repo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("not found"));
        return new User(
                u.getEmail(),
                u.getPassword(),
                u.getRoles().stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r.name())).collect(Collectors.toList())
        );
    }
}
