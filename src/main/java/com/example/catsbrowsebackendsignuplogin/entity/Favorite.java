package com.example.catsbrowsebackendsignuplogin.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "favorite")
public class Favorite {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String catId;

    @Column(nullable=false)
    private String catImage;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private AppUser user;

    public Favorite() {}
    public Favorite(String catId, String catImage, AppUser user) {
        this.catId = catId; this.catImage = catImage; this.user = user;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCatId() { return catId; }
    public void setCatId(String catId) { this.catId = catId; }
    public String getCatImage() { return catImage; }
    public void setCatImage(String catImage) { this.catImage = catImage; }
    public AppUser getUser() { return user; }
    public void setUser(AppUser user) { this.user = user; }
}
