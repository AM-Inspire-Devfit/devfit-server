package com.amcamp.domain.member.api;

import com.amcamp.domain.member.application.MemberService;
import com.amcamp.domain.member.dto.request.NicknameUpdateRequest;
import com.amcamp.domain.member.dto.response.MemberInfoResponse;
import com.amcamp.domain.member.dto.response.SelectedMemberResponse;
import com.amcamp.global.util.CookieUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "회원 API", description = "회원 관련 API입니다.")
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

    @Operation(summary = "팀에 속한 회원 조회", description = "멤버 페이지에서 팀에 속한 회원을 세 명씩 조회합니다.")
    @GetMapping("/list")
    public Slice<SelectedMemberResponse> memberFindThree(
            @Parameter(description = "팀 ID") @RequestParam Long teamId,
            @Parameter(description = "페이지당 멤버 수", example = "1")
                    @RequestParam(value = "size", defaultValue = "3")
                    int pageSize) {
        return memberService.findSelectedMembers(teamId, pageSize);
    }
}
