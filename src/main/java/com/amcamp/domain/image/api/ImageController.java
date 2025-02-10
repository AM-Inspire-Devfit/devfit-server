package com.amcamp.domain.image.api;

import com.amcamp.domain.image.application.ImageService;
import com.amcamp.domain.image.dto.request.MemberImageUploadCompleteRequest;
import com.amcamp.domain.image.dto.request.MemberImageUploadRequest;
import com.amcamp.domain.image.dto.response.PresignedUrlResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "이미지 API", description = "이미지 관련 API입니다.")
@RestController
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @Operation(
            summary = "회원 프로필 이미지 Presigned URL 생성",
            description = "회원 프로필 이미지 Presigned URL을 생성합니다.")
    @PostMapping("/members/me/image/upload-url")
    public PresignedUrlResponse memberImagePresignedUrlCreate(
            @Valid @RequestBody MemberImageUploadRequest request) {
        return imageService.createMemberImagePresignedUrl(request);
    }

    @Operation(
            summary = "회원 프로필 이미지 업로드 완료 처리",
            description = "회원 프로필 이미지의 업로드가 완료되었을 때 호출하시면 됩니다.")
    @PostMapping("/members/me/image/upload-complete")
    public ResponseEntity<Void> memberImageUploadComplete(
            @Valid @RequestBody MemberImageUploadCompleteRequest request) {
        imageService.uploadCompleteMemberImage(request);
        return ResponseEntity.ok().build();
    }
}
