package com.amcamp.domain.member.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String nickname;

    private String profileImageUrl;

    @Embedded
    private OauthInfo oauthInfo;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @Builder(access = AccessLevel.PRIVATE)
    private Member(
            String nickname, String profileImageUrl, OauthInfo oauthInfo, MemberStatus status, MemberRole role) {
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.oauthInfo = oauthInfo;
        this.status = status;
        this.role = role;
    }

    public static Member createMember(String nickname, String profileImageUrl, OauthInfo oauthInfo) {
        return Member.builder()
                .nickname(nickname)
                .profileImageUrl(profileImageUrl)
                .oauthInfo(oauthInfo)
                .status(MemberStatus.NORMAL)
                .role(MemberRole.USER)
                .build();
    }
}
