package com.amcamp.domain.project.domain;

import com.amcamp.domain.common.model.BaseTimeEntity;
import com.amcamp.domain.team.domain.TeamParticipant;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Entity
@NoArgsConstructor
@Getter
public class ProjectParticipant extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_participant_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_participant_id")
    private TeamParticipant teamParticipant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    private String projectNickname;

    private String projectProfile;

    // 프로젝트 내 권한
    @Enumerated(EnumType.STRING)
    private ProjectParticipantRole projectRole;

    // 사용자 지정 역할
    @Nullable
    @Column(name = "position")
    private String ProjectPosition;

    @Builder(access = AccessLevel.PRIVATE)
    private ProjectParticipant(
            TeamParticipant teamParticipant,
            Project project,
            String projectNickname,
            String projectProfile,
            ProjectParticipantRole projectRole) {
        this.teamParticipant = teamParticipant;
        this.project = project;
        this.projectNickname = projectNickname;
        this.projectProfile = projectProfile;
        this.projectRole = projectRole;
    }

    public static ProjectParticipant createProjectParticipant(
            TeamParticipant teamParticipant,
            Project project,
            String projectNickname,
            String projectProfile,
            ProjectParticipantRole projectRole) {
        return ProjectParticipant.builder()
                .teamParticipant(teamParticipant)
                .project(project)
                .projectNickname(projectNickname)
                .projectProfile(projectProfile)
                .projectRole(projectRole)
                .build();
    }

    public void changeRole(ProjectParticipantRole projectRole) {
        this.projectRole = projectRole;
    }
}
