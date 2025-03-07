package com.amcamp.domain.project.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProjectRegistrationStatus {
    PENDING("REQUEST_PENDING"),
    APPROVED("REQUEST_APPROVED"),
    REJECTED("REQUEST_REJECTED");

    private final String status;
}
