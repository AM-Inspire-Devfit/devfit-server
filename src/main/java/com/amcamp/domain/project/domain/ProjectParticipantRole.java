package com.amcamp.domain.project.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProjectParticipantRole {
    ADMIN("PROJECT_ADMIN"),
    USER("PROJECT_USER");

    private final String projectRole;
}
