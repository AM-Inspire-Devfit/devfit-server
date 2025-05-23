package com.amcamp.domain.member.api;

import com.amcamp.domain.member.application.MemberService;
import com.amcamp.domain.member.dto.request.NicknameUpdateRequest;
import com.amcamp.domain.member.dto.response.BasicMemberResponse;
import com.amcamp.domain.member.dto.response.MemberInfoResponse;
import com.amcamp.global.util.CookieUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "1-2. 회원 API", description = "회원 관련 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final CookieUtil cookieUtil;
    private final MemberService memberService;

    @Operation(summary = "로그아웃", description = "로그아웃을 진행합니다.")
    @PostMapping("/logout")
    public ResponseEntity<Void> memberLogout() {
        memberService.logoutMember();
        return ResponseEntity.ok().headers(cookieUtil.deleteRefreshTokenCookie()).build();
    }

    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴를 진행합니다.")
    @DeleteMapping("/withdrawal")
    public ResponseEntity<Void> memberWithdrawal() {
        memberService.withdrawalMember();
        return ResponseEntity.ok().headers(cookieUtil.deleteRefreshTokenCookie()).build();
    }

    @Operation(summary = "회원 닉네임 변경", description = "회원 닉네임을 변경합니다.")
    @PostMapping("/me/nickname")
    public ResponseEntity<Void> memberNicknameUpdate(
            @Valid @RequestBody NicknameUpdateRequest request) {
        memberService.updateMemberNickname(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회원 정보 조회", description = "로그인한 회원 정보를 조회합니다.")
    @GetMapping("/me")
    public MemberInfoResponse memberInfo() {
        return memberService.getMemberInfo();
    }

    @Operation(summary = "팀에 속한 회원 목록 조회", description = "멤버 페이지에서 팀장을 제외한 회원을 모두 조회합니다.")
    @GetMapping("/{teamId}/list")
    public Slice<BasicMemberResponse> memberFindAll(
            @PathVariable Long teamId,
            @Parameter(description = "이전 페이지의 마지막 멤버 ID (첫 페이지는 비워두세요)")
                    @RequestParam(required = false)
                    Long lastMemberId,
            @RequestParam(value = "size", defaultValue = "3") int pageSize) {
        return memberService.findAllMembers(teamId, lastMemberId, pageSize);
    }
}
