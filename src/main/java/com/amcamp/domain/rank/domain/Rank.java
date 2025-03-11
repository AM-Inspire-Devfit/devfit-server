package com.amcamp.domain.rank.domain;

import com.amcamp.domain.common.model.BaseTimeEntity;
import com.amcamp.domain.project.domain.ProjectParticipant;
import com.amcamp.domain.sprint.domain.Sprint;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Rank extends BaseTimeEntity {
    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rank_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sprint_id")
    private Sprint sprint;

    // 기여한 멤버
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id")
    private ProjectParticipant participant;

    // 등위
    private Integer placement;

    // 기여도 점수/비율
    private Double contribution;

    @Builder(access = AccessLevel.PRIVATE)
    Rank(Sprint sprint, ProjectParticipant participant, Integer placement, Double contribution) {
        this.sprint = sprint;
        this.participant = participant;
        this.placement = placement;
        this.contribution = contribution;
    }

    public static Rank createRank(
            Sprint sprint, ProjectParticipant participant, Double contribution) {
        return Rank.builder()
                .sprint(sprint)
                .participant(participant)
                .contribution(contribution)
                .build();
    }

    public void updateContribution(Double contribution) {
        this.contribution = contribution;
    }

    public void updatePlacement(int i) {
        this.placement = placement;
    }
}
