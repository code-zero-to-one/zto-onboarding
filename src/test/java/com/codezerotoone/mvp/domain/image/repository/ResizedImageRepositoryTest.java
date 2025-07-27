package com.codezerotoone.mvp.domain.image.repository;

import com.codezerotoone.mvp.domain.image.constant.ImageSizeType;
import com.codezerotoone.mvp.domain.image.entity.Image;
import com.codezerotoone.mvp.domain.image.entity.ResizedImage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ResizedImageRepositoryTest {

    @Autowired
    ResizedImageRepository resizedImageRepository;

    @Autowired
    ImageRepository imageRepository;

    @Test
    @DisplayName("단순 엔티티 삽입")
    void save_simple() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        // Given

        // Image Entity 생성
        Constructor<Image> imageDefaultConstructor = Image.class.getDeclaredConstructor();
        imageDefaultConstructor.setAccessible(true);
        Image image = imageDefaultConstructor.newInstance();
        image = this.imageRepository.save(image);

        // ResizedImage Entity 생성
        ResizedImage resizedImage = new ResizedImage(image, "file/test.png", ImageSizeType.ORIGINAL);

        // When
        resizedImage = this.resizedImageRepository.save(resizedImage);

        // Then
        Long generatedId = resizedImage.getId();
        Optional<ResizedImage> findResizedImageOp = this.resizedImageRepository.findById(generatedId);
        assertThat(findResizedImageOp).isNotEmpty();
        assertThat(findResizedImageOp.get().getId()).isEqualTo(generatedId);
        assertThat(findResizedImageOp.get().getImage().getId()).isEqualTo(image.getId());
        assertThat(findResizedImageOp.get().getImageSizeType()).isEqualTo(ImageSizeType.ORIGINAL);
        assertThat(findResizedImageOp.get().getResizedImageUrl()).isEqualTo(resizedImage.getResizedImageUrl());
    }
}
