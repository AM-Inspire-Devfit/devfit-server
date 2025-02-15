package com.amcamp.domain.member.application;

import com.amcamp.domain.auth.dao.RefreshTokenRepository;
import com.amcamp.domain.member.domain.Member;
import com.amcamp.domain.member.dto.request.NicknameUpdateRequest;
import com.amcamp.domain.member.dto.response.BasicMemberResponse;
import com.amcamp.domain.member.dto.response.MemberInfoResponse;
import com.amcamp.domain.team.dao.TeamParticipantRepository;
import com.amcamp.global.util.MemberUtil;
import java.util.List;
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
    private final TeamParticipantRepository teamParticipantRepository;

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
    public List<BasicMemberResponse> findSelectedMembers(Long teamId, int pageSize) {
        Member currentMember = memberUtil.getCurrentMember();
        return teamParticipantRepository.findMemberByTeamExceptMember(
                teamId, currentMember.getId(), pageSize);
    }

    public Slice<BasicMemberResponse> findAllMembers(Long teamId, int pageSize) {
        Member currentMember = memberUtil.getCurrentMember();
        return teamParticipantRepository.findMemberByTeamExceptAdmin(
                teamId, currentMember.getId(), pageSize);
    }
}
