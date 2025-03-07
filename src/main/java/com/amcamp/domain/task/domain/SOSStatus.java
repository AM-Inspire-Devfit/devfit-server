package com.amcamp.domain.task.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SOSStatus {
    SOS("TASK_SOS"),
    NOT_SOS("TASK_NOT_SOS");

    private final String sosStatus;
}
