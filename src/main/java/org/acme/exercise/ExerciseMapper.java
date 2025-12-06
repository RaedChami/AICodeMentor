package org.acme.exercise;

import org.acme.exercise.dto.ExerciseDTO;

import java.util.Objects;

public class ExerciseMapper {
    public static ExerciseDTO convertToDTO(Exercise exercise) {
        Objects.requireNonNull(exercise);
        return new ExerciseDTO(
                exercise.getId(),
                exercise.getDescription(),
                exercise.getDifficulty(),
                exercise.getConcepts(),
                exercise.getSignatureAndBody(),
                exercise.getUnitTests(),
                exercise.getSolution()
        );
    }

    public static Exercise convertToEntity(ExerciseDTO exerciseDTO) throws NoSuchFieldException, IllegalAccessException {
        Objects.requireNonNull(exerciseDTO);
        var exercise =  new Exercise(
                exerciseDTO.description(),
                exerciseDTO.difficulty(),
                exerciseDTO.concepts(),
                exerciseDTO.signatureAndBody(),
                exerciseDTO.unitTests(),
                exerciseDTO.solution()
        );
        if (exerciseDTO.id() != null) {
            var idField = Exercise.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(exercise, exerciseDTO.id());
        }

        return exercise;
    }

}
