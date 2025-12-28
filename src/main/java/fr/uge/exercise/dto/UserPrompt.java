package fr.uge.exercise.dto;

import java.util.Objects;

public record UserPrompt(String prompt, long creatorId) {
    public UserPrompt {
        if (creatorId < 0) {
            throw new IllegalArgumentException("creator id is inferior than 0");
        }
        Objects.requireNonNull(prompt);
    }
}
