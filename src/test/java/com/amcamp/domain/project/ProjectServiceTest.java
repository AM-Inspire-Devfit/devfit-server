package com.amcamp.domain.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.amcamp.domain.project.application.ProjectService;
import com.amcamp.domain.project.dao.ProjectRepository;
import com.amcamp.domain.project.domain.Project;
import com.amcamp.domain.project.dto.request.ProjectCreateRequest;
import com.amcamp.domain.project.dto.response.ProjectInfoResponse;
import com.amcamp.domain.team.dao.TeamRepository;
import com.amcamp.domain.team.domain.Team;
import com.amcamp.global.exception.CommonException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class ProjectServiceTest {
    @Autowired private ProjectService projectService;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private TeamRepository teamRepository;

    private ProjectCreateRequest request;

    @BeforeEach
    private void setTeam() {
        Team team = teamRepository.save(Team.createTeam("teamName", "teamDescription"));
        request =
                new ProjectCreateRequest(
                        team.getId(),
                        "projectTitle",
                        "projectDescription",
                        "projectGoal",
                        LocalDateTime.of(2025, 1, 1, 1, 00),
                        LocalDateTime.of(2025, 1, 1, 1, 00));
    }

    @Test
    void 프로젝트를_생성하면_정상적으로_저장된다() {
        ProjectInfoResponse response = projectService.createProject(request);
        Project project = projectRepository.findById(response.projectId()).get();
        assertThat(project.getId()).isEqualTo(response.projectId());
    }

    @Nested
    class 프로젝트_조회 {
        @Test
        void 프로젝트를_ID로_조회하면_정상적으로_반환된다() {
            ProjectInfoResponse response = projectService.createProject(request);
            Project project = projectRepository.findById(response.projectId()).get();
            ProjectInfoResponse foundResponse = projectService.getProjectInfo(project.getId());
            assertThat(response).usingRecursiveComparison().isEqualTo(foundResponse);
        }

        @Test
        void ID가_유효하지_않으면_예외가_발생한다() {
            Long invalidProjectId = Long.MAX_VALUE;
            assertThatThrownBy(() -> projectService.getProjectInfo(invalidProjectId))
                    .isInstanceOf(CommonException.class)
                    .hasMessageContaining("project 를 찾을 수 없습니다.");
        }
    }
}
