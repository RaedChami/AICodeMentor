package fr.uge.exercise;

import fr.uge.exercise.dto.ExerciseDTO;
import fr.uge.login.LoginMapper;

import java.util.Objects;

/**
 * Mapping class for an exercise.
 * Allows to map an exercise into a DTO or vice versa
 */
public class ExerciseMapper {
    /**
     * Converts an exercise entity into a DTO
     * @param exercise entity of an exercise
     * @return DTO of an exercise
     */
    public static ExerciseDTO convertToDTO(Exercise exercise) {
        Objects.requireNonNull(exercise);
        return new ExerciseDTO(
                exercise.getId(),
                exercise.getDescription(),
                exercise.getDifficulty(),
                exercise.getConcepts(),
                exercise.getSignatureAndBody(),
                exercise.getUnitTests(),
                exercise.getSolution(),
                LoginMapper.convertToDTO(exercise.getCreator())
        );
    }

    /**
     * Converts the DTO of an exercise into an entity
     * @param exerciseDTO DTO of an exercise
     * @return entity of an exercise
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
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
