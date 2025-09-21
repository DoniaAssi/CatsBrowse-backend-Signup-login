package com.example.catsbrowsebackendsignuplogin.entity;



import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String catId;
    private String catImage;

    public Favorite() {}

    public Favorite(String catId, String catImage) {
        this.catId = catId;
        this.catImage = catImage;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCatId() { return catId; }
    public void setCatId(String catId) { this.catId = catId; }

    public String getCatImage() { return catImage; }
    public void setCatImage(String catImage) { this.catImage = catImage; }
}