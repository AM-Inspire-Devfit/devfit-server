package com.amcamp.domain.project.api;

import com.amcamp.domain.project.application.ProjectService;
import com.amcamp.domain.project.dto.request.*;
import com.amcamp.domain.project.dto.response.ProjectInfoResponse;
import com.amcamp.domain.project.dto.response.ProjectParticipationInfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
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
            @Valid @RequestBody ProjectCreateRequest projectCreateRequest) {
        projectService.createProject(projectCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(
            summary = "전체 프로젝트 목록 조회",
            description = "팀 ID를 통해 사용자가 참여 중인 프로젝트와 참여 중이지 않은 프로젝트를 나누어 조회합니다.")
    @GetMapping("/{teamId}/list")
    public List<ProjectParticipationInfoResponse> projectListInfo(@PathVariable Long teamId) {
        return projectService.getProjectListInfo(teamId);
    }

    @Operation(summary = "프로젝트 조회", description = "프로젝트 ID를 통해 프로젝트 정보를 조회합니다.")
    @GetMapping("/{projectId}")
    public ProjectInfoResponse projectInfo(@PathVariable Long projectId) {
        return projectService.getProjectInfo(projectId);
    }

    // update
    @Operation(summary = "프로젝트 기본 정보 업데이트", description = "프로젝트 타이틀/목표/상세설정을 수정합니다")
    @PatchMapping("/{projectId}/basic-info")
    public ResponseEntity<Void> projectBasicInfoUpdate(
            @PathVariable Long projectId,
            @Valid @RequestBody ProjectBasicInfoUpdateRequest request) {
        projectService.updateProjectBasicInfo(projectId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "프로젝트 시작/마감기한/진행상태 업데이트", description = "프로젝트 시작/마감기한/진행상태를 수정합니다")
    @PatchMapping("/{projectId}/todo-info")
    public ResponseEntity<Void> projectTodoInfoUpdate(
            @PathVariable Long projectId,
            @Valid @RequestBody ProjectTodoInfoUpdateRequest request) {
        projectService.updateProjectTodoInfo(projectId, request);
        return ResponseEntity.ok().build();
    }

    // delete
    @Operation(summary = "프로젝트 삭제", description = "프로젝트를 삭제합니다.")
    @DeleteMapping("/{projectId}/")
    public ResponseEntity<Void> projectDelete(@PathVariable Long projectId) {
        projectService.deleteProject(projectId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "프로젝트 나가기", description = "프로젝트에 참여중인 프로젝트 참여자 정보를 삭제합니다.")
    @DeleteMapping("/{projectId}/leave")
    public ResponseEntity<Void> projectParticipantDelete(@PathVariable Long projectId) {
        projectService.deleteProjectParticipant(projectId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "admin 권한 양도", description = "프로젝트 Admin 권한을 양도합니다.")
    @PostMapping("/{projectId}/admin/change")
    public ResponseEntity<Void> projectAdminChange(
            @PathVariable Long projectId, @Valid @RequestBody ProjectAdminChangeRequest request) {
        projectService.changeProjectAdmin(projectId, request.newAdminId());
        return ResponseEntity.ok().build();
    }
}
