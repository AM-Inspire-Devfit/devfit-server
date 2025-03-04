package com.amcamp.domain.task.dto.response;

import com.amcamp.domain.member.domain.Member;
import com.amcamp.domain.project.domain.ToDoStatus;
import com.amcamp.domain.task.domain.AssignedStatus;
import com.amcamp.domain.task.domain.Task;
import com.amcamp.domain.task.domain.TaskDifficulty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

public record TaskInfoResponse(
        @Schema(description = "태스크 아이디", example = "1") Long taskId,
        @Schema(description = "태스크 내용", example = "피그마 화면 설계 수정") String description,
        @Schema(description = "태스크 난이도", example = "MID") TaskDifficulty taskDifficulty,
        @Schema(description = "태스크 시작일자", example = "2024-01-01") LocalDate startDt,
        @Schema(description = "태스크 마감일자", example = "2024-01-02") LocalDate dueDt,
        @Schema(description = "태스크 진행현황", example = "1") ToDoStatus toDoStatus,
        @Schema(description = "태스크 담당자", example = "최현태") Member assignee,
        @Schema(description = "태스크 담당현황", example = "1") AssignedStatus assignedStatus) {

    public static TaskInfoResponse from(Task task) {
        Member member = null;
        if (task.getAssignee() != null && task.getAssignedStatus() != AssignedStatus.NOT_ASSIGNED) {
            member = task.getAssignee().getTeamParticipant().getMember();
        }
        return new TaskInfoResponse(
                task.getId(),
                task.getDescription(),
                task.getTaskDifficulty(),
                task.getToDoInfo().getStartDt(),
                task.getToDoInfo().getDueDt(),
                task.getToDoInfo().getToDoStatus(),
                member,
                task.getAssignedStatus());
    }
}
