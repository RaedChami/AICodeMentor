package org.acme.login;

import java.util.Objects;

public record LoginRequestDTO(String name, String lastName) {
    public LoginRequestDTO {
        Objects.requireNonNull(name);
        Objects.requireNonNull(lastName);
    }
}
