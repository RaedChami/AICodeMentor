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
import org.acme.exercise.ExerciseCompiler;
import org.acme.exercise.exception.ExerciseGenerationException;
import org.acme.llm.LlamaService;

@Path("/api/teacher/exercises")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TeacherExerciseResource {

    @Inject
    EntityManager em;
    @Inject
    LlamaService llamaService;
    @Inject
    ExerciseCompiler exerciseCompiler;

    /**
     * @return
     * Returns all created exercises
     */
    @Path("/")
    @GET
    public List<ExerciseDTO> getall() {
        var exercises = em.createNamedQuery("Exercise.findAll", Exercise.class)
                            .getResultList();
        return exercises.stream().map(ExerciseMapper::convertToDTO).collect(Collectors.toList());
    }

    /**
     * @return
     * Returns one given exercise
     */
    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ExerciseDTO get(@PathParam("id") long id) {
        if (id < 0) {
            throw new IllegalArgumentException("exercise ID is < 0");
        }
        var findExercise = em.find(Exercise.class, id);
        if (findExercise == null) {
            throw new NotFoundException("exercise ID not found");
        }
        return ExerciseMapper.convertToDTO(findExercise);
    }

    @Path("/{id}/modify")
    @PUT
    @Transactional
    public ExerciseDTO modify(@PathParam("id") long id, UserModifyPrompt prompt) throws ExerciseGenerationException, IOException {
        Objects.requireNonNull(prompt);
        if (id < 0) {
            throw new IllegalArgumentException("exercise ID is < 0");
        }
        var findExercise = em.find(Exercise.class, id);
        if (findExercise == null) {
            throw new NotFoundException("exercise ID not found");
        }
        int attempts = 0;
        while (attempts <= 5) {
            var modified = llamaService.modifyExercise(findExercise, prompt.modificationDescription());
            if (modified.isPresent()) {
                var exercise = modified.orElseThrow();
                if (exerciseCompiler.compile(exercise)) {
                    exercise.setId(id);
                    return ExerciseMapper.convertToDTO(em.merge(exercise));
                }
            }
            attempts++;
        }
        throw new ExerciseGenerationException("Impossible de générer une modification valide après 5 tentatives");
    }

    @Path("/{id}")
    @DELETE
    @Transactional
    public void delete(@PathParam("id") long id) {
        if (id < 0) {
            throw new IllegalArgumentException("exercise ID is < 0");
        }
        var exercise = em.find(Exercise.class, id);
        if (exercise != null) {
            em.remove(exercise);
        } else {
            throw new NotFoundException("Exercise with id " + id + " not found");
        }
    }

}
