package com.amcamp.domain.auth.application;

import com.amcamp.domain.auth.domain.OauthProvider;
import com.amcamp.domain.auth.dto.request.AuthCodeRequest;
import com.amcamp.domain.auth.dto.response.SocialLoginResponse;
import com.amcamp.domain.member.dao.MemberRepository;
import com.amcamp.domain.member.domain.Member;
import com.amcamp.domain.member.domain.MemberStatus;
import com.amcamp.domain.member.domain.OauthInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final KakaoService kakaoService;
    private final GoogleService googleService;
    private final JwtTokenService jwtTokenService;
    private final IdTokenVerifier idTokenVerifier;
    private final MemberRepository memberRepository;

    public SocialLoginResponse socialLoginMember(AuthCodeRequest request, OauthProvider provider) {
        String idToken = getIdToken(request.code(), provider);

        OidcUser oidcUser = idTokenVerifier.getOidcUser(idToken, provider);

        Optional<Member> optionalMember = findByOidcUser(oidcUser);
        Member member = optionalMember.orElseGet(() -> saveMember(oidcUser, provider));

		if (member.getStatus() == MemberStatus.DELETED){
			member.reEnroll();
		}

        return getLoginResponse(member);
    }

    private SocialLoginResponse getLoginResponse(Member member) {
        String accessToken = jwtTokenService.createAccessToken(member.getId(), member.getRole());
        String refreshToken = jwtTokenService.createRefreshToken(member.getId());
        return SocialLoginResponse.of(accessToken, refreshToken);
    }

    private String getIdToken(String code, OauthProvider provider) {
        return switch (provider) {
            case GOOGLE -> googleService.getIdToken(code);
            case KAKAO -> kakaoService.getIdToken(code);
        };
    }

    private Optional<Member> findByOidcUser(OidcUser oidcUser) {
        OauthInfo oauthInfo = extractOauthInfo(oidcUser);
        return memberRepository.findByOauthInfo(oauthInfo);
    }

    private Member saveMember(OidcUser oidcUser, OauthProvider provider) {
        OauthInfo oauthInfo = extractOauthInfo(oidcUser);
        String nickname = getDisplayName(oidcUser, provider);

        Member member = Member.createMember(nickname, oidcUser.getPicture(), oauthInfo);
        return memberRepository.save(member);
    }

    private OauthInfo extractOauthInfo(OidcUser oidcUser) {
        return OauthInfo.createOauthInfo(
                oidcUser.getSubject(), oidcUser.getIssuer().toString());
    }

    private String getDisplayName(OidcUser oidcUser, OauthProvider provider) {
        return switch (provider) {
            case GOOGLE -> (String) oidcUser.getClaims().get("name");
            case KAKAO -> (String) oidcUser.getClaims().get("nickname");
        };
    }
}
