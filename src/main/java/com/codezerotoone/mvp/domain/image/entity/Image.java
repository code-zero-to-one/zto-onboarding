package com.codezerotoone.mvp.domain.image.entity;

import com.codezerotoone.mvp.domain.common.BaseEntity;
import com.codezerotoone.mvp.domain.image.entity.dto.ResizedImageInfo;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "image")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Image extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    @OneToMany(mappedBy = "image", cascade = { CascadeType.PERSIST, CascadeType.REMOVE }) // TODO: 고아객체 삭제?
    private List<ResizedImage> resizedImages = new ArrayList<>();

    private String location;

    private LocalDateTime deletedAt = null;

    public static Image create(String location, ResizedImageInfo... resizedImages) {
        Image image = new Image();
        image.location = location;
        image.resizedImages = Arrays.stream(resizedImages)
                .map((dto) -> new ResizedImage(image, dto.resizedImageUrl(), dto.imageSizeType()))
                .toList();
        return image;
    }

    public static Image create(String location, List<ResizedImageInfo> resizedImages) {
        Image image = new Image();
        image.location = location;
        image.resizedImages = resizedImages.stream()
                .map((dto) -> new ResizedImage(image, dto.resizedImageUrl(), dto.imageSizeType()))
                .toList();
        return image;
    }

    public static Image getReference(Long id) {
        Image image = new Image();
        image.imageId = id;
        return image;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }
}
