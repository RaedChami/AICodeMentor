package org.acme.dto;

import java.util.Objects;

public record UserModifyPrompt(String modificationDescription) {
    public UserModifyPrompt {
        Objects.requireNonNull(modificationDescription);
    }
}
