package com.amcamp.global.util;

import com.amcamp.domain.member.dao.MemberRepository;
import com.amcamp.domain.member.domain.Member;
import com.amcamp.domain.member.domain.MemberStatus;
import com.amcamp.global.exception.CommonException;
import com.amcamp.global.exception.errorcode.AuthErrorCode;
import com.amcamp.global.exception.errorcode.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberUtil {

    private final MemberRepository memberRepository;

    public Member getCurrentMember() {
        Member member =
                memberRepository
                        .findById(getCurrentMemberId())
                        .orElseThrow(() -> new CommonException(MemberErrorCode.MEMBER_NOT_FOUND));

        if (member.getStatus() == MemberStatus.DELETED) {
            throw new CommonException(MemberErrorCode.MEMBER_ALREADY_DELETED);
        }

        return member;
    }

    private Long getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        try {
            return Long.parseLong(authentication.getName());
        } catch (Exception e) {
            throw new CommonException(AuthErrorCode.AUTH_NOT_FOUND);
        }
    }
}
