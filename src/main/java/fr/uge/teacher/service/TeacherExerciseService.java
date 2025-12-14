package fr.uge.teacher.service;

import fr.uge.exercise.exception.ExerciseUnauthorizedAccess;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import fr.uge.exercise.Exercise;
import fr.uge.exercise.dto.UserPrompt;
import fr.uge.exercise.exception.ExerciseGenerationException;
import fr.uge.exercise.service.ExerciseCompiler;
import fr.uge.llm.LlamaService;

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

    /**
     * Reusable precondition method for the id of an exercise
     * @param id serial number of the exercise
     */
    private void validateExerciseId(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("exercise ID is < 0");
        }
    }

    /**
     * Validates that the user is the creator of the exercise
     * @param exercise The exercise to validate
     * @param userId The user ID to check
     * @throws IllegalArgumentException if the user is not the creator
     */
    private void validateExerciseOwnership(Exercise exercise, long userId) {
        Objects.requireNonNull(exercise);
        if (userId <= 0) {
            throw new IllegalArgumentException("user ID is < 0");
        }
        if (exercise.getCreator() == null || exercise.getCreator().getId() != userId) {
            throw new ExerciseUnauthorizedAccess("You are unauthorized to modify this exercise.");
        }
    }

    /**
     * Returns all existing exercises
     * @return The list of existing exercises
     */
    public List<Exercise> getAllExercises() {
        return entityManager.createNamedQuery("Exercise.findAll", Exercise.class)
                .getResultList();
    }

    public List<Exercise> getExercisesByUserId(long userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("user ID is < 0");
        }
        return entityManager
                .createQuery(
                        "SELECT e FROM Exercise e WHERE e.creator.id = :userId"
                        , Exercise.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    /**
     * Returns an exercise corresponding to the given id
     * @param id serial number of the exercise
     * @return The exercise if it exists
     */
    public Exercise getExercise(long id) {
        validateExerciseId(id);
        var findExercise = entityManager.find(Exercise.class, id);
        if (findExercise == null) {
            throw new NotFoundException("exercise ID not found");
        }
        return findExercise;
    }

    /**
     * Returns an AI-generated modified exercise corresponding to the given id
     * using the user's prompt.
     * Fails after 5 attempts of generation.
     * @param id serial number of the exercise
     * @param prompt The user's prompt for the modification of the exercise
     * @return The modified exercise if success of the generation
     * @throws IOException Propagated exception from writing the files for the compilation
     */
    public Exercise modifyExerciseById(long id, UserPrompt prompt) throws IOException, ExerciseUnauthorizedAccess {
        Objects.requireNonNull(prompt);
        validateExerciseId(id);
        var findExercise = getExercise(id);
        validateExerciseOwnership(findExercise, prompt.creatorId());
        for (var i = 0; i < MAX_ATTEMPTS; i++) {
            var result = generateValidModifiedExercise(findExercise, prompt, id);
            if (result != null) { // Valid modified exercise has been generated
                return result;
            }
        }
        throw new ExerciseGenerationException("Impossible de générer une modification valide après 5 tentatives");
    }

    /**
     * Generates a modified exercise and checks the compilation result of the JUnit tests and solution program
     * from the modified exercise.
     * @param findExercise The exercise to be modified
     * @param prompt The user's prompt for the modification of the exercise
     * @param id serial number of the exercise
     * @return The modified exercise if success of compilation, or null in case of failure
     * @throws IOException Propagated exception from writing the files for the compilation
     */
    private Exercise generateValidModifiedExercise(Exercise findExercise, UserPrompt prompt, long id) throws IOException {
        var modified = llamaService.modifyExercise(findExercise, prompt.prompt());
        if (modified.isEmpty()) {
            return null;
        }
        if (!exerciseCompiler.compile(modified.orElseThrow())) {
            return null;
        }
        return saveModifiedExercise(modified.orElseThrow(), id);
    }

    /**
     * Merges the modified exercise
     * @param exercise The modified exercise
     * @param id serial number of the exercise
     * @return The merged exercise
     */
    @Transactional
    Exercise saveModifiedExercise(Exercise exercise, long id) {
        Objects.requireNonNull(exercise);
        var existingExercise = entityManager.find(Exercise.class, id);
        if (existingExercise != null && existingExercise.getCreator() != null) {
            exercise.setCreator(existingExercise.getCreator());
        }
        exercise.setId(id);
        return entityManager.merge(exercise);
    }

    /**
     * Deletes an exercise corresponding to a given id
     * @param id serial number of the exercise
     */
    @Transactional
    public void deleteExercise(long id, long userId) throws ExerciseUnauthorizedAccess {
        validateExerciseId(id);
        var exercise = getExercise(id);
        validateExerciseOwnership(exercise, userId);
        entityManager.remove(getExercise(id));
    }
}
