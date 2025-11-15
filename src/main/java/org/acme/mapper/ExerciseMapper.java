package org.acme.mapper;

import org.acme.dto.ExerciseDTO;
import org.acme.model.Exercise;

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

    public static Exercise convertToEntity(ExerciseDTO exerciseDTO) {
        Objects.requireNonNull(exerciseDTO);
        return new Exercise(
                exerciseDTO.description(),
                exerciseDTO.difficulty(),
                exerciseDTO.concepts(),
                exerciseDTO.signatureAndBody(),
                exerciseDTO.unitTests(),
                exerciseDTO.solution()
        );
    }
}
