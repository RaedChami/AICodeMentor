package org.acme.teacher;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import org.acme.model.Exercise;
import org.acme.llm.JlamaService;
import org.acme.resource.ExerciseCompiler;

@Path("/api/teacher/generate")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TeacherExerciseResource {

    @Inject
    EntityManager em;
    @Inject
    JlamaService jlamaService;
    @Inject
    ExerciseCompiler exerciseCompiler;
    @POST
    public Exercise generate(Exercise exercise) throws IOException {
        Objects.requireNonNull(exercise);
        var finalExercise = compile(exercise.description);
        while (finalExercise == null) {
            finalExercise = compile(exercise.description);
        }
        return finalExercise;
    }


    @POST
    @Transactional
    @Path("/save")
    public Exercise save(Exercise exercise) {
        Objects.requireNonNull(exercise);
        return (exercise.id != null) ? em.merge(exercise) : returnPersist(exercise);
    }

    private Exercise returnPersist(Exercise exercise) {
        Objects.requireNonNull(exercise);
        em.persist(exercise);
        return exercise;
    }

    private Exercise compile(String description) {
        Objects.requireNonNull(description);
        try {
            var generated = jlamaService.generateExercise(description);
            exerciseCompiler.createTemporaryDirectory();
            var testPath = exerciseCompiler.createTemporaryFiles(generated.unitTests);
            var solutionPath = exerciseCompiler.createTemporaryFiles(generated.solution);
            if (!exerciseCompiler.compileCode(generated.unitTests, testPath) || !exerciseCompiler.compileCode(generated.solution, solutionPath)) {
                System.out.println("Compilation failed, Exercise regeneration");
                return null;
            }
            return generated;
        } finally {
            exerciseCompiler.cleanDirectory();
        }
    }

    @Path("/exercises")
    @GET
    public List<Exercise> get() {
      return em.createNamedQuery("Exercise.findAll", Exercise.class)
              .getResultList();
    }

    @Path("/exercises/{id}")
    @DELETE
    @Transactional
    public void delete(@PathParam("id") long id) {
        Exercise exercise = em.find(Exercise.class, id);
        if (exercise != null) {
            em.remove(exercise);
        } else {
            throw new NotFoundException("Exercise with id " + id + " not found");
        }
    }

    @Path("/exercises/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Exercise getById(@PathParam("id") long id) {
        return em.find(Exercise.class, id);
    }
}
