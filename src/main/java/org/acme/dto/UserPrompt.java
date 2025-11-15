package org.acme.dto;

import java.util.Objects;

public record UserPrompt(String prompt) {
    public UserPrompt {
        Objects.requireNonNull(prompt);
    }
}
