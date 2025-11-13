package org.acme.resource;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.acme.Teacher;
import org.acme.model.Exercise;
import org.acme.llm.JlamaService;
import jakarta.persistence.EntityManager;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Path("/api/exercises")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ExerciseResource {

    @Inject
    EntityManager em;
    @Inject
    JlamaService jlamaService;
    @Inject
    ExerciseCompiler exerciseCompiler;
    @POST
    public Exercise create(Exercise exercise) throws IOException {
        Objects.requireNonNull(exercise);
        var generated = jlamaService.generateExercise(exercise.description);
        exercise.description = generated.description;
        exerciseCompiler.createTemporaryDirectory();
        var testPath = exerciseCompiler.createTemporaryFiles(generated.unitTests);
        var solutionPath = exerciseCompiler.createTemporaryFiles(generated.solution);
        if (!exerciseCompiler.compileCode(generated.unitTests, testPath) || !exerciseCompiler.compileCode(generated.solution, solutionPath)) {
            System.out.println("Compilation failed");
            exerciseCompiler.cleanDirectory();
        }
        persistExercise(generated);
        exerciseCompiler.cleanDirectory();
        return exercise;
    }

    @Transactional
    public void persistExercise(Exercise exercise) {
        Objects.requireNonNull(exercise);
        em.persist(exercise);
    }

    @GET
    public List<Exercise> get() {
        return em.createNamedQuery("Exercise.findAll", Exercise.class)
                .getResultList();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public void delete(@PathParam("id") long id) {
        Exercise exercise = em.find(Exercise.class, id);
        if (exercise != null) {
            em.remove(exercise);
        } else {
            throw new NotFoundException("Exercise with id " + id + " not found");
        }
    }

}
