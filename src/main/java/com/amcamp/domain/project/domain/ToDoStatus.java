package com.amcamp.domain.project.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ToDoStatus {
    NOT_STARTED("STATUS_NOT_STARTED"),
    ON_GOING("STATUS_ON_GOING"),
    COMPLETED("STATUS_COMPLETED");

    private final String status;
}
