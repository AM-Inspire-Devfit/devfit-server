package com.amcamp.domain.project.api;

import com.amcamp.domain.project.application.ProjectService;
import com.amcamp.domain.project.dto.request.ProjectCreateRequest;
import com.amcamp.domain.project.dto.response.ProjectInfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "프로젝트 API", description = "프로젝트 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    @Operation(summary = "프로젝트 조회", description = "프로젝트 ID를 통해 프로젝트 정보를 조회합니다.")
    @PostMapping("/project")
    public ProjectInfoResponse projectCreate(
            @RequestBody ProjectCreateRequest projectCreateRequest) {
        return projectService.createProject(projectCreateRequest);
    }

    @Operation(summary = "프로젝트 조회", description = "프로젝트 ID를 통해 프로젝트 정보를 조회합니다.")
    @GetMapping("/project/{projectId}")
    public ProjectInfoResponse projectInfo(@Valid @PathVariable Long projectId) {
        return projectService.getProjectInfo(projectId);
    }
}
