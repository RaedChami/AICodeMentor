package org.acme.exercise.dto;

import org.acme.exercise.Difficulty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record ExerciseDTO(Long id, String description, Difficulty difficulty, List<String> concepts, String signatureAndBody,
                          String unitTests, String solution) {
    public ExerciseDTO {
        if (id != null && id <= 0) {
            throw new IllegalArgumentException("exercise id is lower than 0");
        }
        Objects.requireNonNull(description);
        Objects.requireNonNull(difficulty);
        Objects.requireNonNull(concepts);
        Objects.requireNonNull(signatureAndBody);
        Objects.requireNonNull(unitTests);
        Objects.requireNonNull(solution);
        concepts = new ArrayList<>(concepts);
    }

    @Override
    public List<String> concepts() {
        return new ArrayList<>(concepts);
    }
}
