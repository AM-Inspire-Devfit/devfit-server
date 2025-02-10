package com.amcamp.domain.image.application;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amcamp.domain.image.dao.ImageRepository;
import com.amcamp.domain.image.domain.Image;
import com.amcamp.domain.image.domain.ImageFileExtension;
import com.amcamp.domain.image.dto.request.MemberImageUploadCompleteRequest;
import com.amcamp.domain.image.dto.request.MemberImageUploadRequest;
import com.amcamp.domain.image.dto.response.PresignedUrlResponse;
import com.amcamp.domain.member.domain.Member;
import com.amcamp.global.common.constants.UrlConstants;
import com.amcamp.global.exception.CommonException;
import com.amcamp.global.exception.errorcode.ImageErrorCode;
import com.amcamp.global.util.MemberUtil;
import com.amcamp.infra.config.s3.S3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Transactional
@Service
@RequiredArgsConstructor
public class ImageService {

    private final MemberUtil memberUtil;
    private final S3Properties s3Properties;
    private final ImageRepository imageRepository;
    private final AmazonS3 amazonS3;

    public PresignedUrlResponse createMemberImagePresignedUrl(MemberImageUploadRequest request) {
        final Member currentMember = memberUtil.getCurrentMember();

        String imageKey = generateUUID();
        String imageName = createImageFileName(currentMember.getId(), imageKey, request.imageFileExtension());
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                generatePresignedUrlRequest(s3Properties.bucket(), imageName, request.imageFileExtension().getExtension());

        String presignedUrl = amazonS3.generatePresignedUrl(generatePresignedUrlRequest).toString();

        imageRepository.save(
                Image.createImage(
                        currentMember.getId(),
                        imageKey,
                        request.imageFileExtension()));

        return new PresignedUrlResponse(presignedUrl);
    }

    public void uploadCompleteMemberImage(MemberImageUploadCompleteRequest request) {
        final Member currentMember = memberUtil.getCurrentMember();

        String imageUrl = null;
        if (request.imageFileExtension() != null) {
            Image image = findImage(currentMember.getId(), request.imageFileExtension());
            imageUrl = createReadImageUrl(currentMember.getId(), image.getImageKey(), image.getImageFileExtension());
        }

        currentMember.updateProfileImageUrl(imageUrl);
    }

    private GeneratePresignedUrlRequest generatePresignedUrlRequest(
            String bucket, String imageName, String imageFileExtension) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucket, imageName, HttpMethod.PUT)
                        .withKey(imageName)
                        .withContentType("image/" + imageFileExtension)
                        .withExpiration(getPresignedUrlExpiration());

        generatePresignedUrlRequest.addRequestParameter(
                Headers.S3_CANNED_ACL, CannedAccessControlList.PublicRead.toString());

        return generatePresignedUrlRequest;
    }

    private String generateUUID() {
        return UUID.randomUUID().toString();
    }

    private String createImageFileName(Long memberId, String imageKey, ImageFileExtension imageFileExtension) {
        return memberId + "/" + imageKey + "." + imageFileExtension.getExtension();
    }

    private Date getPresignedUrlExpiration() {
        Date expiration = new Date();
        long expTime = expiration.getTime();
        expTime += TimeUnit.MINUTES.toMillis(3);
        expiration.setTime(expTime);

        return expiration;
    }

    private Image findImage(Long memberId, ImageFileExtension imageFileExtension) {
        return imageRepository.findLatestByMemberIdAndExtension(memberId, imageFileExtension)
                .orElseThrow(() -> new CommonException(ImageErrorCode.IMAGE_NOT_FOUND));
    }

    private String createReadImageUrl(Long memberId, String imageKey, ImageFileExtension imageFileExtension) {
        return UrlConstants.IMAGE_URL + "/" + createImageFileName(memberId, imageKey, imageFileExtension);
    }
}
