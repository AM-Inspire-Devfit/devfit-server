package com.amcamp.domain.project.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProjectParticipantRole {
    ADMIN("PROJECT_ADMIN"),
    MEMBER("PROJECT_MEMBER");

    private final String projectRole;
}
