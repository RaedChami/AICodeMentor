package fr.uge.exercise.dto;

import fr.uge.exercise.Difficulty;
import fr.uge.login.Login;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * DTO of an exercise
 * @param id serial number of the exercise
 * @param description description for the exercise
 * @param difficulty difficulty level of the exercise
 * @param concepts programming concepts of the exercise
 * @param signatureAndBody signature & body of the exercise
 * @param unitTests JUnit class for the exercise
 * @param solution solution example for the exercise
 * @param creator author of the exercise
 */
public record ExerciseDTO(Long id, String description, Difficulty difficulty, List<String> concepts, String signatureAndBody,
                          String unitTests, String solution, Login creator) {
    public ExerciseDTO {
        if (id != null && id <= 0) {
            throw new IllegalArgumentException("exercise id is inferior than 0");
        }
        Objects.requireNonNull(description);
        Objects.requireNonNull(difficulty);
        Objects.requireNonNull(concepts);
        Objects.requireNonNull(signatureAndBody);
        Objects.requireNonNull(unitTests);
        Objects.requireNonNull(solution);
        concepts = new ArrayList<>(Objects.requireNonNull(concepts));
    }

    @Override
    public List<String> concepts() {
        return new ArrayList<>(concepts);
    }
}
