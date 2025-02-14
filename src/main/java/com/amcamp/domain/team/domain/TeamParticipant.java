package com.amcamp.domain.team.domain;

import com.amcamp.domain.common.model.BaseTimeEntity;
import com.amcamp.domain.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamParticipant extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_participant_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Enumerated(EnumType.STRING)
    private TeamParticipantRole role;

    @Builder(access = AccessLevel.PRIVATE)
    private TeamParticipant(Member member, Team team, TeamParticipantRole role) {
        this.member = member;
        this.team = team;
        this.role = role;
    }

    public static TeamParticipant createParticipant(
            Member member, Team team, TeamParticipantRole role) {
        return TeamParticipant.builder().member(member).team(team).role(role).build();
    }
}
