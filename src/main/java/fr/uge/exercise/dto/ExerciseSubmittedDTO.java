package fr.uge.exercise.dto;

import java.util.Objects;

public record ExerciseSubmittedDTO(
        long id,
        long loginId,
        long exerciseId,
        String solutionSubmitted
) {
    public ExerciseSubmittedDTO {
        if (id < 0) {
            throw new IllegalArgumentException("exercise id is inferior than 0");
        }
        if (loginId < 0) {
            throw new IllegalArgumentException("exercise id is inferior than 0");
        }
        if (exerciseId < 0) {
            throw new IllegalArgumentException("exercise id is inferior than 0");
        }
        Objects.requireNonNull(solutionSubmitted, "solution is inferior null");
    }
}

