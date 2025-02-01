package com.amcamp.domain.auth.api;

import com.amcamp.domain.auth.application.AuthService;
import com.amcamp.domain.auth.domain.OauthProvider;
import com.amcamp.domain.auth.dto.request.AuthCodeRequest;
import com.amcamp.domain.auth.dto.response.SocialLoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "인증 API", description = "인증 관련 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입 및 로그인", description = "회원가입 및 로그인을 진행합니다.")
    @PostMapping("/social-login")
    public SocialLoginResponse memberSocialLogin(
            @RequestParam(name = "oauthProvider") OauthProvider provider,
            @RequestBody AuthCodeRequest request) {
        return authService.socialLoginMember(request, provider);
    }
}
