package com.amcamp.domain.task.api;

import com.amcamp.domain.task.application.TaskService;
import com.amcamp.domain.task.dto.request.TaskBasicInfoUpdateRequest;
import com.amcamp.domain.task.dto.request.TaskCreateRequest;
import com.amcamp.domain.task.dto.request.TaskToDoInfoUpdateRequest;
import com.amcamp.domain.task.dto.response.TaskInfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "6. 태스크 API", description = "태스크  관련 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;

    @Operation(summary = "태스크 생성", description = "태스크를 생성합니다.")
    @PostMapping("/create")
    public ResponseEntity<Void> taskCreate(@Valid @RequestBody TaskCreateRequest request) {
        taskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "태스크 기본 정보 수정", description = "태스크 기본 정보를 수정합니다.")
    @PatchMapping("/{taskId}/basic-info")
    public TaskInfoResponse taskUpdateBasic(
            @PathVariable Long taskId, @Valid @RequestBody TaskBasicInfoUpdateRequest request) {
        return taskService.updateTaskBasicInfo(taskId, request);
    }

    @Operation(summary = "태스크 완료", description = "태스크 일정 및 진행 상태를 수정합니다.")
    @PatchMapping("/{taskId}/todo-info")
    public TaskInfoResponse taskUpdateToDo(
            @PathVariable Long taskId, @Valid @RequestBody TaskToDoInfoUpdateRequest request) {
        return taskService.updateTaskToDoInfo(taskId, request);
    }

    @Operation(summary = "태스크 담당자 할당", description = "태스크 담당 상태를 수정합니다.")
    @PostMapping("/{taskId}/assign")
    public TaskInfoResponse taskAssign(@PathVariable Long taskId) {
        return taskService.assignTask(taskId);
    }

    @Operation(summary = "태스크 삭제", description = "태스크를 삭제합니다.")
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> taskDelete(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
