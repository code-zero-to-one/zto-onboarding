package com.codezerotoone.mvp.domain.image.repository;

import com.codezerotoone.mvp.domain.image.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {

    @Override
    @Query("""
            SELECT i
            FROM Image i
            WHERE i.deleteYn = false
                AND i.id = :imageId
            """)
    Optional<Image> findById(@Param("imageId") Long imageId);
}
