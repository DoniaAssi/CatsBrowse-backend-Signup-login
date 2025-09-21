package com.example.catsbrowsebackendsignuplogin.dto;



public class FavoriteRequestDTO {
    private String catId;
    private String catImage;

    public String getCatId() { return catId; }
    public void setCatId(String catId) { this.catId = catId; }

    public String getCatImage() { return catImage; }
    public void setCatImage(String catImage) { this.catImage = catImage; }
}