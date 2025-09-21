package com.example.catsbrowsebackendsignuplogin.service;



import com.example.catsbrowsebackendsignuplogin.dto.FavoriteRequestDTO;
import com.example.catsbrowsebackendsignuplogin.dto.FavoriteResponseDTO;
import com.example.catsbrowsebackendsignuplogin.entity.Favorite;
import com.example.catsbrowsebackendsignuplogin.exception.ResourceNotFoundException;
import com.example.catsbrowsebackendsignuplogin.repository.FavoriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;

    @Autowired
    public FavoriteService(FavoriteRepository favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }

    public Page<FavoriteResponseDTO> getFavorites(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return favoriteRepository.findAll(pageable)
                .map(fav -> new FavoriteResponseDTO(fav.getId(), fav.getCatId(), fav.getCatImage()));
    }

    @Transactional
    public FavoriteResponseDTO createFavorite(FavoriteRequestDTO requestDTO) {
        Favorite saved = favoriteRepository.save(new Favorite(requestDTO.getCatId(), requestDTO.getCatImage()));
        return new FavoriteResponseDTO(saved.getId(), saved.getCatId(), saved.getCatImage());
    }

    @Transactional
    public void removeFavorite(Long id) {
        if (!favoriteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Favorite not found with id: " + id);
        }
        favoriteRepository.deleteById(id);
    }
}