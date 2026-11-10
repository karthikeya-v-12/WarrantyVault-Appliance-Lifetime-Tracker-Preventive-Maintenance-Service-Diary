package com.warrantyvault.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HouseholdAsset> assets = new ArrayList<>();

    public User() {
    }

    public User(Long id, String name, String email, LocalDateTime createdAt, List<HouseholdAsset> assets) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
        this.assets = (assets != null) ? assets : new ArrayList<>();
    }

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<HouseholdAsset> getAssets() {
        return assets;
    }

    public void setAssets(List<HouseholdAsset> assets) {
        this.assets = (assets != null) ? assets : new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static class UserBuilder {
        private Long id;
        private String name;
        private String email;
        private LocalDateTime createdAt;
        private List<HouseholdAsset> assets = new ArrayList<>();

        UserBuilder() {}

        public UserBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UserBuilder name(String name) {
            this.name = name;
            return this;
        }

        public UserBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public UserBuilder assets(List<HouseholdAsset> assets) {
            this.assets = assets;
            return this;
        }

        public User build() {
            return new User(id, name, email, createdAt, assets);
        }
    }
}
