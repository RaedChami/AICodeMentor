package org.acme.teacher;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.acme.exercise.dto.ExerciseDTO;
import org.acme.exercise.dto.UserModifyPrompt;
import org.acme.exercise.ExerciseMapper;
import org.acme.exercise.Exercise;
import org.acme.exercise.service.ExerciseCompiler;
import org.acme.exercise.exception.ExerciseGenerationException;
import org.acme.llm.LlamaService;
import org.jboss.resteasy.reactive.RestPath;

@Path("/api/teacher/exercises")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TeacherExerciseResource {

    private final EntityManager entityManager;
    private final LlamaService llamaService;
    private final ExerciseCompiler exerciseCompiler;
    @Inject
    TeacherExerciseResource(EntityManager entityManager, LlamaService llamaService, ExerciseCompiler exerciseCompiler) {
        this.entityManager = Objects.requireNonNull(entityManager);
        this.llamaService = Objects.requireNonNull(llamaService);
        this.exerciseCompiler = Objects.requireNonNull(exerciseCompiler);
    }

    /**
     * @return all existing exercises
     */
    @Path("/")
    @GET
    public List<ExerciseDTO> getall() {
        var exercises = entityManager.createNamedQuery("Exercise.findAll", Exercise.class)
                            .getResultList();
        return exercises.stream().map(ExerciseMapper::convertToDTO).collect(Collectors.toList());
    }

    /**
     * @return an existing exercise
     */
    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ExerciseDTO get(@RestPath("id") long id) {
        if (id < 0) {
            throw new IllegalArgumentException("exercise ID is < 0");
        }
        var findExercise = entityManager.find(Exercise.class, id);
        if (findExercise == null) {
            throw new NotFoundException("exercise ID not found");
        }
        return ExerciseMapper.convertToDTO(findExercise);
    }

    /**
     * Modifies an existing exercise
     * @param id ID of an exercise
     * @param prompt Prompt from user
     * @return a Modified exercise
     * @throws ExerciseGenerationException If generation failed more than 5 times
     * @throws IOException Propagated exception from compiling class
     */
    @Path("/{id}/modify")
    @PUT
    @Transactional
    public ExerciseDTO modify(@RestPath("id") long id, UserModifyPrompt prompt) throws ExerciseGenerationException, IOException {
        Objects.requireNonNull(prompt);
        if (id < 0) {
            throw new IllegalArgumentException("exercise ID is < 0");
        }
        var findExercise = entityManager.find(Exercise.class, id);
        if (findExercise == null) {
            throw new NotFoundException("exercise ID not found");
        }
        int attempts = 0;
        while (attempts <= 5) {
            var modified = llamaService.modifyExercise(findExercise, prompt.modificationDescription());
            if (modified.isPresent()) { // compiles the programs of the modified exercise
                var exercise = modified.orElseThrow();
                if (exerciseCompiler.compile(exercise)) {
                    exercise.setId(id);
                    return ExerciseMapper.convertToDTO(entityManager.merge(exercise));
                }
            }
            attempts++;
        }
        throw new ExerciseGenerationException("Impossible de générer une modification valide après 5 tentatives");
    }

    /**
     * Deletes an existing exercise
     * @param id ID of an exercise
     */
    @Path("/{id}")
    @DELETE
    @Transactional
    public void delete(@RestPath("id") long id) {
        if (id < 0) {
            throw new IllegalArgumentException("exercise ID is < 0");
        }
        var exercise = entityManager.find(Exercise.class, id);
        if (exercise != null) {
            entityManager.remove(exercise);
        } else {
            throw new NotFoundException("Exercise with id " + id + " not found");
        }
    }

}
