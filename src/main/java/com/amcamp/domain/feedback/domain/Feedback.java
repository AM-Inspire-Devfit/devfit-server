package com.amcamp.domain.feedback.domain;

import com.amcamp.domain.project.domain.ProjectParticipant;
import com.amcamp.domain.sprint.domain.Sprint;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_participant_id")
    private ProjectParticipant participant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sprint_id")
    private Sprint sprint;

    @Column(length = 600)
    private String message;

    @Builder(access = AccessLevel.PRIVATE)
    private Feedback(ProjectParticipant participant, Sprint sprint, String message) {
        this.participant = participant;
        this.sprint = sprint;
        this.message = message;
    }

    public static Feedback createFeedback(
            ProjectParticipant participant, Sprint sprint, String message) {
        return Feedback.builder().participant(participant).sprint(sprint).message(message).build();
    }
}
