package com.codezerotoone.mvp.domain.image.entity;

import com.codezerotoone.mvp.domain.common.BaseGeneralEntity;
import com.codezerotoone.mvp.domain.image.entity.dto.ResizedImageInfo;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "image")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Image extends BaseGeneralEntity {
    // 변경 사유: 원본 이미지와 연결되지 않은 경우 의미없는 데이터이므로, DB에 튜플을 남기지 않는 편이 장기적으로 낫다고 보입니다.
    @OneToMany(mappedBy = "image", cascade = {CascadeType.PERSIST}, orphanRemoval = true)
    private List<ResizedImage> resizedImages = new ArrayList<>();

    private String location;

    private Image(Long id) {
        updateId(id);
    }

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

    // 변경 사유: 메서드명과 역할 불일치
    public static Image of(Long id) {
        // 변경 사유: 생성 로직 간소화
        return new Image(id);
    }

    public void delete() {
        deleteEntity();
    }

    public boolean isDeleted() {
        return isDeleted();
    }
}
