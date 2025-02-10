package com.amcamp.domain.member.domain;

import com.amcamp.domain.common.model.BaseTimeEntity;
import com.amcamp.global.exception.CommonException;
import com.amcamp.global.exception.errorcode.MemberErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String nickname;

    private String profileImageUrl;

    @Embedded private OauthInfo oauthInfo;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @Builder(access = AccessLevel.PRIVATE)
    private Member(
            String nickname,
            String profileImageUrl,
            OauthInfo oauthInfo,
            MemberStatus status,
            MemberRole role) {
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.oauthInfo = oauthInfo;
        this.status = status;
        this.role = role;
    }

    public static Member createMember(
            String nickname, String profileImageUrl, OauthInfo oauthInfo) {
        return Member.builder()
                .nickname(nickname)
                .profileImageUrl(profileImageUrl)
                .oauthInfo(oauthInfo)
                .status(MemberStatus.NORMAL)
                .role(MemberRole.USER)
                .build();
    }

    public void withdrawal() {
        if (this.status == MemberStatus.DELETED) {
            throw new CommonException(MemberErrorCode.MEMBER_ALREADY_DELETED);
        }

        this.status = MemberStatus.DELETED;
    }

    public void reEnroll() {
        this.status = MemberStatus.NORMAL;
    }

    public void updateProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }
}
