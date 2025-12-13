package fr.uge.exercise.dto;

public record ExerciseSubmittedDTO(
        Long id,
        Long loginId,
        Long exerciseId,
        String solutionSubmitted
) {}

