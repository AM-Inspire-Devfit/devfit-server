package com.amcamp.domain.member.application;

import com.amcamp.domain.auth.dao.RefreshTokenRepository;
import com.amcamp.domain.member.dao.MemberRepository;
import com.amcamp.domain.member.domain.Member;
import com.amcamp.domain.member.dto.request.NicknameUpdateRequest;
import com.amcamp.domain.member.dto.response.MemberInfoResponse;
import com.amcamp.domain.member.dto.response.SelectedMemberResponse;
import com.amcamp.global.util.MemberUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberUtil memberUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;

    public void logoutMember() {
        Member currentMember = memberUtil.getCurrentMember();
        refreshTokenRepository
                .findById(currentMember.getId())
                .ifPresent(refreshTokenRepository::delete);
    }

    public void withdrawalMember() {
        Member currentMember = memberUtil.getCurrentMember();

        refreshTokenRepository
                .findById(currentMember.getId())
                .ifPresent(refreshTokenRepository::delete);

        currentMember.withdrawal();
    }

    public void updateMemberNickname(NicknameUpdateRequest request) {
        Member currentMember = memberUtil.getCurrentMember();

        currentMember.updateNickname(request.nickname());
    }

    @Transactional(readOnly = true)
    public MemberInfoResponse getMemberInfo() {
        Member currentMember = memberUtil.getCurrentMember();
        return MemberInfoResponse.from(currentMember);
    }

    @Transactional(readOnly = true)
    public Slice<SelectedMemberResponse> findSelectedMembers(Long teamId, int pageSize) {
        Member currentMember = memberUtil.getCurrentMember();
        return memberRepository.findMemberByTeamExceptMember(
                teamId, currentMember.getId(), pageSize);
    }
}
