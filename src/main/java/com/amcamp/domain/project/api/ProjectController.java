package com.amcamp.domain.project.api;

import com.amcamp.domain.project.application.ProjectService;
import com.amcamp.domain.project.dto.request.*;
import com.amcamp.domain.project.dto.response.ProjectInfoResponse;
import com.amcamp.domain.project.dto.response.ProjectListInfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "4. 프로젝트 API", description = "프로젝트 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    @Operation(summary = "프로젝트 생성", description = "새로운 프로젝트를 생성합니다.")
    @PostMapping("/create")
    public ResponseEntity<Void> projectCreate(
            @RequestBody ProjectCreateRequest projectCreateRequest) {
        projectService.createProject(projectCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(
            summary = "전체 프로젝트 목록 조회",
            description = "팀 ID를 통해 사용자가 참여 중인 프로젝트와 참여 중이지 않은 프로젝트를 나누어 조회합니다.")
    @GetMapping("/{teamId}/list")
    public ProjectListInfoResponse projectListInfo(@PathVariable Long teamId) {
        return projectService.getProjectListInfo(teamId);
    }

    @Operation(summary = "프로젝트 조회", description = "프로젝트 ID를 통해 프로젝트 정보를 조회합니다.")
    @GetMapping("/{projectId}")
    public ProjectInfoResponse projectInfo(@PathVariable Long projectId) {
        return projectService.getProjectInfo(projectId);
    }

    // update
    @Operation(summary = "프로젝트 타이틀 업데이트", description = "프로젝트 타이틀을 수정합니다")
    @PostMapping("/{projectId}/title")
    public ResponseEntity<Void> projectTitleUpdate(
            @RequestBody ProjectTextInfoUpdateRequest request) {
        projectService.updateProjectTitle(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "프로젝트 목표 업데이트", description = "프로젝트 목표를 수정합니다")
    @PostMapping("/{projectId}/goal")
    public ResponseEntity<Void> projectGoalUpdate(
            @RequestBody ProjectTextInfoUpdateRequest request) {
        projectService.updateProjectGoal(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "프로젝트 상세설명 업데이트", description = "프로젝트 상세설명을 수정합니다")
    @PostMapping("/{projectId}/desc")
    public ResponseEntity<Void> projectDescriptionUpdate(
            @RequestBody ProjectTextInfoUpdateRequest request) {
        projectService.updateProjectDescription(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "프로젝트 시작/마감/진행상태 업데이트", description = "프로젝트 시작/마감/진행상태를 수정합니다")
    @PostMapping("/{projectId}/todo")
    public ResponseEntity<Void> projectTodoInfoUpdate(
            @RequestBody ProjectTodoInfoUpdateRequest request) {
        projectService.updateProjectTodoInfo(request);
        return ResponseEntity.ok().build();
    }
}
