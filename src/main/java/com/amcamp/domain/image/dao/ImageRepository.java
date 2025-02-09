package com.amcamp.domain.image.dao;

import com.amcamp.domain.image.domain.Image;
import com.amcamp.domain.image.domain.ImageFileExtension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    @Query(
            "select i from Image i where i.memberId = :memberId and i.imageFileExtension = :imageFileExtension " +
                    "order by i.id desc limit 1"
    )
    Optional<Image> findLatestByMemberIdAndExtension(Long memberId, ImageFileExtension imageFileExtension);
}
