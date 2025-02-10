package com.amcamp.domain.image.application;

import com.amcamp.domain.image.dao.ImageRepository;
import com.amcamp.domain.image.domain.Image;
import com.amcamp.domain.image.domain.ImageFileExtension;
import com.amcamp.domain.image.dto.request.MemberImageUploadCompleteRequest;
import com.amcamp.domain.image.dto.request.MemberImageUploadRequest;
import com.amcamp.domain.image.dto.response.PresignedUrlResponse;
import com.amcamp.domain.member.dao.MemberRepository;
import com.amcamp.domain.member.domain.Member;
import com.amcamp.domain.member.domain.OauthInfo;
import com.amcamp.global.exception.CommonException;
import com.amcamp.global.exception.errorcode.ImageErrorCode;
import com.amcamp.global.exception.errorcode.MemberErrorCode;
import com.amcamp.global.security.PrincipalDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class ImageServiceTest {

	@Autowired
	private ImageService imageService;
	@Autowired
	private ImageRepository imageRepository;
	@Autowired
	private MemberRepository memberRepository;

	@BeforeEach
	void setUp() {
		Member member =
			memberRepository.save(
				Member.createMember("testNickname", "testProfileImageUrl",
					OauthInfo.createOauthInfo("testOauthId", "testOauthProvider")));

		UserDetails userDetails = new PrincipalDetails(member.getId(), member.getRole());
		UsernamePasswordAuthenticationToken token =
			new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(token);
	}

	@Nested
	class 회원_프로필_이미지_PresignedUrl_생성_시 {
		@Test
		void 회원이_존재하지_않는다면_예외가_발생한다() {
			// given
			memberRepository.deleteAll();
			MemberImageUploadRequest request = new MemberImageUploadRequest(ImageFileExtension.JPEG);

			// when & then
			assertThatThrownBy(() -> imageService.createMemberImagePresignedUrl(request))
				.isInstanceOf(CommonException.class)
				.hasMessage(MemberErrorCode.MEMBER_NOT_FOUND.getMessage());
		}

		@Test
		void 회원이_존재하면_PresignedUrl이_생성된다() {
			// given
			MemberImageUploadRequest request = new MemberImageUploadRequest(ImageFileExtension.JPEG);

			// when
			PresignedUrlResponse response = imageService.createMemberImagePresignedUrl(request);

			// then
			assertThat(response.presignedUrl()).isNotNull();
			assertThat(response.presignedUrl()).startsWith("https://s3.ap-northeast-2.amazonaws.com/");
		}
	}

	@Nested
	class 회원_프로필_이미지_업로드_완료_처리_시 {
		@Test
		void 회원이_업로드한_이미지가_존재하지_않는다면_예외가_발생한다() {
			// given
			MemberImageUploadCompleteRequest request = new MemberImageUploadCompleteRequest(ImageFileExtension.JPEG);

			// when & then
			assertThatThrownBy(() -> imageService.uploadCompleteMemberImage(request))
				.isInstanceOf(CommonException.class)
				.hasMessage(ImageErrorCode.IMAGE_NOT_FOUND.getMessage());
		}

		@Test
		void 이미지가_존재하면_회원의_프로필_이미지가_변경된다() {
			// given
			imageRepository.save(Image.createImage(1L, "testImageKey", ImageFileExtension.JPEG));

			MemberImageUploadCompleteRequest request = new MemberImageUploadCompleteRequest(ImageFileExtension.JPEG);

			// when
			imageService.uploadCompleteMemberImage(request);

			// then
			Member member = memberRepository.findById(1L).get();
			assertThat(member.getProfileImageUrl())
				.isEqualTo("https://devfit-bucket.s3.ap-northeast-2.amazonaws.com/1/testImageKey.jpeg");
		}
	}
}
