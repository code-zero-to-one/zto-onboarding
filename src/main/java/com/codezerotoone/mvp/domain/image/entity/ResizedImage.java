package com.codezerotoone.mvp.domain.image.entity;

import com.codezerotoone.mvp.domain.image.constant.ImageSizeType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "resized_image")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ResizedImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resizedImageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    private Image image;

    private String resizedImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "image_size_type", columnDefinition = "VARCHAR(10)")
    private ImageSizeType imageSizeType;

    public LocalDateTime deletedAt = null;

    public ResizedImage(Image image, String resizedImageUrl, ImageSizeType imageSizeType) {
        this.image = image;
        this.resizedImageUrl = resizedImageUrl;
        this.imageSizeType = imageSizeType;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    public boolean isNotDeleted() {
        return !isDeleted();
    }

    public String getFullResizedImageUrl() {
        String location = this.image.getLocation();
        return (location.endsWith("/") ? location : location + "/")
                + (this.resizedImageUrl.startsWith("/") ? this.resizedImageUrl.substring(1) : this.resizedImageUrl);
    }
}
