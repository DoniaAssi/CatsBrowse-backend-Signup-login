package com.example.catsbrowsebackendsignuplogin.repository;



import com.example.catsbrowsebackendsignuplogin.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {


    void deleteByCatId(String catId);
}