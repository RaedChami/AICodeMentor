package fr.uge.exercise.dto;

import java.util.Objects;

public record UserPrompt(String prompt, Long creatorId) {
    public UserPrompt {
        Objects.requireNonNull(prompt);
        Objects.requireNonNull(creatorId);
    }
}
