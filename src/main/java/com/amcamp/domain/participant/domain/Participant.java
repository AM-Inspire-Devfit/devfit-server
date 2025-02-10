package com.amcamp.domain.participant.domain;

import com.amcamp.domain.common.model.BaseTimeEntity;
import com.amcamp.domain.member.domain.Member;
import com.amcamp.domain.team.domain.Team;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Participant extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Enumerated(EnumType.STRING)
    private ParticipantRole role;

    @Builder(access = AccessLevel.PRIVATE)
    private Participant(Member member, Team team, ParticipantRole role) {
        this.member = member;
        this.team = team;
        this.role = role;
    }

    public static Participant createParticipant(Member member, Team team, ParticipantRole role) {
        return Participant.builder().member(member).team(team).role(role).build();
    }
}
