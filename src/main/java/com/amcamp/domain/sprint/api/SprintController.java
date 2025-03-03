package com.amcamp.domain.sprint.api;

import com.amcamp.domain.sprint.application.SprintService;
import com.amcamp.domain.sprint.dto.request.SprintBasicUpdateRequest;
import com.amcamp.domain.sprint.dto.request.SprintCreateRequest;
import com.amcamp.domain.sprint.dto.request.SprintToDoUpdateRequest;
import com.amcamp.domain.sprint.dto.response.SprintInfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "5. 스프린트 API", description = "스프린트 관련 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/sprints")
public class SprintController {

    private final SprintService sprintService;

    @Operation(summary = "스프린트 생성", description = "스프린트를 생성합니다.")
    @PostMapping("/create")
    public ResponseEntity<Void> sprintCreate(@Valid @RequestBody SprintCreateRequest request) {
        sprintService.createSprint(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "스프린트 기본 정보 수정", description = "스프린트의 기본 정보(제목, 목표)를 수정합니다.")
    @PatchMapping("/{sprintId}/basic-info")
    public SprintInfoResponse sprintUpdateBasic(
            @PathVariable Long sprintId, @Valid @RequestBody SprintBasicUpdateRequest request) {
        return sprintService.updateSprintBasicInfo(sprintId, request);
    }

    @Operation(summary = "스프린트 일정 및 진행 상태 수정", description = "스프린트의 일정 및 진행 상태를 수정합니다.")
    @PatchMapping("/{sprintId}/todo-info")
    public SprintInfoResponse sprintUpdateToDo(
            @PathVariable Long sprintId, @Valid @RequestBody SprintToDoUpdateRequest request) {
        return sprintService.updateSprintToDoInfo(sprintId, request);
    }
}
