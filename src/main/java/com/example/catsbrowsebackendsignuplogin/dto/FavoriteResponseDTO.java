package com.example.catsbrowsebackendsignuplogin.dto;



public class FavoriteResponseDTO {
    private Long id;
    private String catId;
    private String catImage;

    public FavoriteResponseDTO() {}

    public FavoriteResponseDTO(Long id, String catId, String catImage) {
        this.id = id;
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