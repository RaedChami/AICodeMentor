package fr.uge.login.dto;

import fr.uge.login.Role;

import java.util.Objects;

/**
 * DTO of a user
 * @param id serial number of the user ID
 * @param name first name of the user
 * @param lastName last name of the user
 * @param role role of the user (teacher or student)
 */
public record LoginDTO(Long id, String name, String lastName, Role role) {
    public LoginDTO {
        if (id != null && id < 0) {
            throw new IllegalArgumentException("login id is inferior than 0");
        }
        Objects.requireNonNull(name);
        Objects.requireNonNull(lastName);
        Objects.requireNonNull(role);
    }
}
