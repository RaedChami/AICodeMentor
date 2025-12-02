package org.acme.exercise.dto;

import java.util.Objects;

public record UserPrompt(String prompt) {
    public UserPrompt {
        Objects.requireNonNull(prompt);
    }
}
