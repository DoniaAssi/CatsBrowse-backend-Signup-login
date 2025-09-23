package com.example.catsbrowsebackendsignuplogin.service;


import com.example.catsbrowsebackendsignuplogin.dto.FavoriteRequestDTO;
import com.example.catsbrowsebackendsignuplogin.dto.FavoriteResponseDTO;
import com.example.catsbrowsebackendsignuplogin.entity.AppUser;
import com.example.catsbrowsebackendsignuplogin.entity.Favorite;
import com.example.catsbrowsebackendsignuplogin.exception.ResourceNotFoundException;
import com.example.catsbrowsebackendsignuplogin.repository.FavoriteRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final CurrentUserProvider currentUserProvider;

    public FavoriteService(FavoriteRepository favoriteRepository, CurrentUserProvider currentUserProvider) {
        this.favoriteRepository = favoriteRepository;
        this.currentUserProvider = currentUserProvider;
    }

    public Page<FavoriteResponseDTO> getFavorites(int page, int size) {
        AppUser me = currentUserProvider.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return favoriteRepository.findAllByUserId(me.getId(), pageable)
                .map(f -> new FavoriteResponseDTO(f.getId(), f.getCatId(), f.getCatImage()));
    }

    @Transactional
    public FavoriteResponseDTO createFavorite(FavoriteRequestDTO req) {
        AppUser me = currentUserProvider.getCurrentUser();
        Favorite saved = favoriteRepository.save(new Favorite(req.getCatId(), req.getCatImage(), me));
        return new FavoriteResponseDTO(saved.getId(), saved.getCatId(), saved.getCatImage());
    }

    @Transactional
    public void removeFavorite(Long id) {
        AppUser me = currentUserProvider.getCurrentUser();
        if (!favoriteRepository.existsByIdAndUserId(id, me.getId()))
            throw new ResourceNotFoundException("Favorite not found for current user: " + id);
        favoriteRepository.deleteByIdAndUserId(id, me.getId());
    }
}
