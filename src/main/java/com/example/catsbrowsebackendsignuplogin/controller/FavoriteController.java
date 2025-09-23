package com.example.catsbrowsebackendsignuplogin.controller;
import com.example.catsbrowsebackendsignuplogin.dto.FavoriteRequestDTO;
import com.example.catsbrowsebackendsignuplogin.dto.FavoriteResponseDTO;
import com.example.catsbrowsebackendsignuplogin.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorite")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Autowired
    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }


    @GetMapping
    public FavoritePageResponse getFavorites(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size) {
        Page<FavoriteResponseDTO> pageData = favoriteService.getFavorites(page, size);
        return new FavoritePageResponse(
                pageData.getContent(),
                pageData.getNumber(),
                pageData.getTotalPages(),
                pageData.getTotalElements()
        );
    }

    @PostMapping
    public FavoriteResponseDTO createFavorite(@RequestBody FavoriteRequestDTO requestDTO) {
        return favoriteService.createFavorite(requestDTO);
    }

    @DeleteMapping("/{id}")
    public void removeFavorite(@PathVariable Long id) {
        favoriteService.removeFavorite(id);
    }


    public static class FavoritePageResponse {
        private List<FavoriteResponseDTO> content;
        private int pageNumber;
        private int totalPages;
        private long totalElements;

        public FavoritePageResponse(List<FavoriteResponseDTO> content, int pageNumber, int totalPages, long totalElements) {
            this.content = content;
            this.pageNumber = pageNumber;
            this.totalPages = totalPages;
            this.totalElements = totalElements;
        }

        public List<FavoriteResponseDTO> getContent() { return content; }
        public int getPageNumber() { return pageNumber; }
        public int getTotalPages() { return totalPages; }
        public long getTotalElements() { return totalElements; }
    }
}
