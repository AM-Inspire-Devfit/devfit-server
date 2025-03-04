package com.amcamp.domain.project.domain;

import com.amcamp.global.exception.CommonException;
import com.amcamp.global.exception.errorcode.GlobalErrorCode;
import jakarta.annotation.Nullable;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ToDoInfo {
    // 시작&마감일
    private LocalDate startDt; // 시작 일자
    private LocalDate dueDt; // 마감 일자

    @Enumerated(value = EnumType.STRING)
    private ToDoStatus toDoStatus; // 진행 상태

    @Builder(access = AccessLevel.PRIVATE)
    private ToDoInfo(
            @Nullable LocalDate startDt,
            @Nullable LocalDate dueDt,
            @Nullable ToDoStatus toDoStatus) {
        this.startDt = startDt;
        this.dueDt = dueDt;
        this.toDoStatus = toDoStatus;
    }

    public static ToDoInfo createToDoInfo(LocalDate startDt, LocalDate dueDt) {
        validateDates(startDt, dueDt);
        return ToDoInfo.builder()
                .startDt(startDt)
                .dueDt(dueDt)
                .toDoStatus(ToDoStatus.NOT_STARTED)
                .build();
    }

    public void updateToDoInfo(LocalDate startDt, LocalDate dueDt, ToDoStatus status) {
        if (startDt != null) {
            validateDates(startDt, this.dueDt);
            this.startDt = startDt;
        }
        if (dueDt != null) {
            validateDates(this.startDt, dueDt);
            this.dueDt = dueDt;
        }
        if (status != null) this.toDoStatus = status;
    }

    private static void validateDates(LocalDate startDt, LocalDate dueDt) {
        if (startDt == null || dueDt == null) return;
        if (startDt.isAfter(dueDt) || startDt.isBefore(LocalDate.now())) {
            throw new CommonException(GlobalErrorCode.INVALID_DATE_ERROR);
        }
    }
}
