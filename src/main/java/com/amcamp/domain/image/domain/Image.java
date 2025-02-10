package com.amcamp.domain.image.domain;

import com.amcamp.domain.common.model.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    private Long memberId;

    private String imageKey;

    @Enumerated(EnumType.STRING)
    private ImageFileExtension imageFileExtension;

    @Builder(access = AccessLevel.PRIVATE)
    private Image(Long memberId, String imageKey, ImageFileExtension imageFileExtension) {
        this.memberId = memberId;
        this.imageKey = imageKey;
        this.imageFileExtension = imageFileExtension;
    }

    public static Image createImage(
            Long memberId, String imageKey, ImageFileExtension imageFileExtension) {
        return Image.builder()
                .memberId(memberId)
                .imageKey(imageKey)
                .imageFileExtension(imageFileExtension)
                .build();
    }
}
