package com.codezerotoone.mvp.domain.image.service;

import com.codezerotoone.mvp.domain.image.entity.Image;
import com.codezerotoone.mvp.domain.image.entity.dto.ResizedImageInfo;
import com.codezerotoone.mvp.domain.image.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;

    /**
     * 이미지 저장.
     *
     * @param imageInfos 동일한 이미지를 여러 사이즈로 리사이징한 이미지의 정보
     * @return 생성된 <code>Image</code> 엔티티의 ID
     */
    public Long saveImage(String location, List<ResizedImageInfo> imageInfos) {
        Image newImage = Image.create(location, imageInfos);
        newImage = this.imageRepository.save(newImage);
        return newImage.getId();
    }
}
