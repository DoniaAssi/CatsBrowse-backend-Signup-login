package com.example.catsbrowsebackendsignuplogin.repository;



import com.example.catsbrowsebackendsignuplogin.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Page<Favorite> findAllByUserId(Long userId, Pageable pageable);
    boolean existsByIdAndUserId(Long id, Long userId);
    void deleteByIdAndUserId(Long id, Long userId);
    boolean existsByCatIdAndUserId(String catId, Long userId);
}