package com.amcamp.domain.task.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TaskDifficulty {
    HIGH("DIFFICULTY_HIGH"),
    MID("DIFFICULTY_MID"),
    LOW("DIFFICULTY_LOW");

    private final String taskDifficulty;
}
