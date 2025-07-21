package com.codezerotoone.mvp.domain.image.repository;

import com.codezerotoone.mvp.domain.image.constant.ImageSizeType;
import com.codezerotoone.mvp.domain.image.entity.Image;
import com.codezerotoone.mvp.domain.image.entity.ResizedImage;
import com.codezerotoone.mvp.domain.image.entity.dto.ResizedImageInfo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ImageRepositoryTest {

    @Autowired
    ImageRepository imageRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    @DisplayName("Cascade 조건에 의해 ResizedImage도 저장됨")
    void testSave_cascadePersist() {
        // Given
        List<ResizedImageInfo> resizedImageInfos = List.of(
                new ResizedImageInfo("image/test1.png", ImageSizeType.ORIGINAL),
                new ResizedImageInfo("image/test2.png", ImageSizeType.ORIGINAL),
                new ResizedImageInfo("image/test3.png", ImageSizeType.ORIGINAL),
                new ResizedImageInfo("image/test4.png", ImageSizeType.ORIGINAL)
        );

        Image image = Image.create("http://localhost:8080", resizedImageInfos);

        // When
        image = this.imageRepository.save(image);

        // Then
        // ResizedImage에 제대로 엔티티가 삽입됐는지 확인
        List<ResizedImage> result = this.em.createQuery("""
                        SELECT ri
                        FROM ResizedImage ri
                        WHERE ri.deletedAt IS NULL AND ri.image.imageId = :imageId
                        """, ResizedImage.class)
                .setParameter("imageId", image.getImageId())
                .getResultList();

        // 사이즈 확인
        assertThat(result).size().isEqualTo(resizedImageInfos.size());

        // ResizedImage에 들어간 값 확인
        assertThat(result).extracting("resizedImageUrl", "imageSizeType")
                .containsExactlyInAnyOrder(
                        resizedImageInfos.stream()
                                .map((e) -> Tuple.tuple(e.resizedImageUrl(), e.imageSizeType()))
                                .toArray(Tuple[]::new)
                );

        // Image 엔티티가 제대로 들어갔는지 확인
        assertThat(this.imageRepository.findById(image.getImageId())).isNotEmpty();
    }
}