package org.acme.dto;

import org.acme.model.Difficulty;
import org.acme.model.Exercise;

import java.util.List;
import java.util.Objects;

public record ExerciseDTO(Long id, String description, Difficulty difficulty, List<String> concepts, String signatureAndBody,
                          String unitTests, String solution) {
    public ExerciseDTO {
        if (id != null && id < 0) {
            throw new IllegalArgumentException("exercise id is lower than 0");
        }
        Objects.requireNonNull(description);
        Objects.requireNonNull(difficulty);
        Objects.requireNonNull(concepts);
        Objects.requireNonNull(signatureAndBody);
        Objects.requireNonNull(unitTests);
        Objects.requireNonNull(solution);
    }
}
