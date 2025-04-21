package com.amcamp.domain.image.dto.request;

import com.amcamp.domain.image.domain.ImageFileExtension;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record MemberImageUploadRequest(
        @NotNull(message = "이미지 파일 확장자는 비워둘 수 없습니다.")
                @Schema(description = "이미지 파일 확장자", defaultValue = "JPEG")
                ImageFileExtension imageFileExtension) {}
