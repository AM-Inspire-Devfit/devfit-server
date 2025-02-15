package com.amcamp.domain.project.domain;

import com.amcamp.global.exception.CommonException;
import com.amcamp.global.exception.errorcode.GlobalErrorCode;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ToDoInfo {
    // 시작&마감일
    private LocalDateTime startDt; // 시작 일자
    private LocalDateTime dueDt; // 마감 일자

    @Enumerated(value = EnumType.STRING)
    private ToDoStatus toDoStatus; // 진행 상태

    @Builder(access = AccessLevel.PRIVATE)
    private ToDoInfo(LocalDateTime startDt, LocalDateTime dueDt, ToDoStatus toDoStatus) {
        this.startDt = startDt;
        this.dueDt = dueDt;
        this.toDoStatus = toDoStatus;
    }

    public static ToDoInfo createToDoInfo(LocalDateTime startDt, LocalDateTime dueDt) {
        if (startDt.isAfter(dueDt) || startDt.isBefore(LocalDateTime.now())) {
            throw new CommonException(GlobalErrorCode.INVALID_DATE_ERROR);
        }
        return ToDoInfo.builder()
                .startDt(startDt)
                .dueDt(dueDt)
                .toDoStatus(ToDoStatus.NOT_STARTED)
                .build();
    }
}
