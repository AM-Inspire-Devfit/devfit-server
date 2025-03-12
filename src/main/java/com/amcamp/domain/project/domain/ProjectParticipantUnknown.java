package com.amcamp.domain.project.domain;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ProjectParticipantUnknown {
    NICKNAME("UNKNOWN_PROJECT_NICKNAME"),
    PROFILE_URL("UNKNOWN_PROJECT_PROFILE_URL");

    private final String s;
}
