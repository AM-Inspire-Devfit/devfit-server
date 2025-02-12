package com.amcamp.domain.task.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AssignedStatus {
    ASSIGNED("TASK_STATUS_ASSIGNED"),
    NOT_ASSIGNED("TASK_STATUS_NOT_ASSIGNED");

    private final String taskStatus;
}
