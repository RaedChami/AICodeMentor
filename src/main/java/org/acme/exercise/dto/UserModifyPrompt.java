package org.acme.exercise.dto;

import java.util.Objects;

public record UserModifyPrompt(String modificationDescription) {
    public UserModifyPrompt {
        Objects.requireNonNull(modificationDescription);
    }
}
