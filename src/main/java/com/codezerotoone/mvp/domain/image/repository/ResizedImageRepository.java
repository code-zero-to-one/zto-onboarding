package com.codezerotoone.mvp.domain.image.repository;

import com.codezerotoone.mvp.domain.image.entity.ResizedImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResizedImageRepository extends JpaRepository<ResizedImage, Long> {
}
