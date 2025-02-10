package com.amcamp.domain.member.application;

import com.amcamp.domain.auth.dao.RefreshTokenRepository;
import com.amcamp.domain.member.domain.Member;
import com.amcamp.domain.member.dto.request.NicknameUpdateRequest;
import com.amcamp.global.util.MemberUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberUtil memberUtil;
    private final RefreshTokenRepository refreshTokenRepository;

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
}
