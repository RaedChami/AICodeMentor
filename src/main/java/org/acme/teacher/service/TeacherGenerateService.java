package org.acme.teacher.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.acme.exercise.Exercise;
import org.acme.exercise.ExerciseMapper;
import org.acme.exercise.dto.ExerciseDTO;
import org.acme.exercise.dto.UserPrompt;
import org.acme.exercise.exception.ExerciseGenerationException;
import org.acme.exercise.service.ExerciseCompiler;
import org.acme.llm.LlamaService;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
public class TeacherGenerateService {

    private final static int MAX_ATTEMPTS = 10;
    private final EntityManager entityManager;
    private final LlamaService llamaService;
    private final ExerciseCompiler exerciseCompiler;

    @Inject
    TeacherGenerateService(EntityManager entityManager, LlamaService llamaService, ExerciseCompiler exerciseCompiler) {
        this.entityManager = Objects.requireNonNull(entityManager);
        this.llamaService = Objects.requireNonNull(llamaService);
        this.exerciseCompiler = Objects.requireNonNull(exerciseCompiler);
    }

    public Exercise generateExerciseService(UserPrompt prompt) throws ExerciseGenerationException, IOException {
        Objects.requireNonNull(prompt);
        var generatedExercise = llamaService.generateExercise(prompt.prompt());
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            if (generatedExercise.isPresent()) {
                var exercise = generatedExercise.orElseThrow();
                if (exerciseCompiler.compile(exercise)) {
                    return exercise;
                }
                generatedExercise = llamaService.modifyExercise(exercise,
                        "L'exercice ne compile pas. CORRIGEZ les erreurs de compilation.");
            } else {
                generatedExercise = regenerateExercise(prompt);
            }
        }
        throw new ExerciseGenerationException("Impossible de générer un exercice valide après 10 tentatives.");
    }

    private Optional<Exercise> regenerateExercise(UserPrompt prompt) {
        return llamaService.generateExercise(prompt.prompt());
    }

    @Transactional
    public Exercise saveGeneratedExercise(ExerciseDTO dtoExercise) throws NoSuchFieldException, IllegalAccessException {
        Objects.requireNonNull(dtoExercise);
        var exercise = ExerciseMapper.convertToEntity(dtoExercise);
        entityManager.persist(exercise);
        return exercise;
    }
}
