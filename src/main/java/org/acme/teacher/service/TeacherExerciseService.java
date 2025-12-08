package org.acme.teacher.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.acme.exercise.Exercise;
import org.acme.exercise.dto.UserPrompt;
import org.acme.exercise.exception.ExerciseGenerationException;
import org.acme.exercise.service.ExerciseCompiler;
import org.acme.llm.LlamaService;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class TeacherExerciseService {

    private final static int MAX_ATTEMPTS = 5;
    private final EntityManager entityManager;
    private final LlamaService llamaService;
    private final ExerciseCompiler exerciseCompiler;

    @Inject
    TeacherExerciseService(EntityManager entityManager, LlamaService llamaService, ExerciseCompiler exerciseCompiler) {
        this.entityManager = Objects.requireNonNull(entityManager);
        this.llamaService = Objects.requireNonNull(llamaService);
        this.exerciseCompiler = Objects.requireNonNull(exerciseCompiler);
    }

    private void validateExerciseId(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("exercise ID is < 0");
        }
    }

    public List<Exercise> getAllExercises() {
        return entityManager.createNamedQuery("Exercise.findAll", Exercise.class)
                            .getResultList();
    }

    public Exercise getExercise(long id) {
        validateExerciseId(id);
        var findExercise = entityManager.find(Exercise.class, id);
        if (findExercise == null) {
            throw new NotFoundException("exercise ID not found");
        }
        return findExercise;
    }

    public Exercise modifyExerciseById(long id, UserPrompt prompt) throws IOException {
        Objects.requireNonNull(prompt);
        validateExerciseId(id);
        var findExercise = getExercise(id);
        for (var i = 0; i < MAX_ATTEMPTS; i++) {
            var result = generateValidModifiedExercise(findExercise, prompt, id);
            if (result != null) { // Valid modified exercise has been generated
                return result;
            }
        }
        throw new ExerciseGenerationException("Impossible de générer une modification valide après 5 tentatives");
    }

    private Exercise generateValidModifiedExercise(Exercise findExercise, UserPrompt prompt, long id) throws IOException {
        var modified = llamaService.modifyExercise(findExercise, prompt.prompt());
        if (modified.isEmpty()) {
            return null;
        }
        var exercise = modified.orElseThrow();
        if (!exerciseCompiler.compile(exercise)) {
            return null;
        }
        return saveModifiedExercise(exercise, id);
    }

    @Transactional
    Exercise saveModifiedExercise(Exercise exercise, long id) {
        Objects.requireNonNull(exercise);
        exercise.setId(id);
        return entityManager.merge(exercise);
    }

    @Transactional
    public void deleteExercise(long id) {
        validateExerciseId(id);
        entityManager.remove(getExercise(id));
    }
}
