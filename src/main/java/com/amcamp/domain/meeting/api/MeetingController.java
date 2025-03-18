package com.amcamp.domain.meeting.api;

import com.amcamp.domain.meeting.application.MeetingService;
import com.amcamp.domain.meeting.dto.MeetingCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "8. 팀 미팅 API", description = "팀 미팅 관련 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/meetings")
public class MeetingController {

    private final MeetingService meetingService;

    @Operation(summary = "미팅 생성", description = "새로운 미팅을 생성합니다.")
    @PostMapping("/{meetingId}")
    public ResponseEntity<Void> meetingCreate(@RequestBody MeetingCreateRequest request) {
        meetingService.createMeeting(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
