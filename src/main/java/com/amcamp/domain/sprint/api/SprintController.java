package com.amcamp.domain.sprint.api;

import com.amcamp.domain.sprint.application.SprintService;
import com.amcamp.domain.sprint.dto.request.SprintCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "스프린트 API", description = "스프린트 관련 API입니다.")
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
}
