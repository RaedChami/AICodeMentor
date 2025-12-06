package org.acme.login.dto;

import org.acme.login.Role;

import java.util.Objects;

public record LoginDTO(Long id, String name, String lastName, Role role) {
    public LoginDTO {
        if (id != null && id < 0) {
            throw new IllegalArgumentException("login id is lower than 0");
        }
        Objects.requireNonNull(name);
        Objects.requireNonNull(lastName);
        Objects.requireNonNull(role);
    }
}
