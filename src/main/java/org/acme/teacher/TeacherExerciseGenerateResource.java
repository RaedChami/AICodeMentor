package org.acme.teacher;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.acme.llm.JlamaService;
import org.acme.model.Exercise;
import org.acme.resource.ExerciseCompiler;

import java.io.IOException;
import java.util.Objects;

@Path("/api/teacher/generate")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TeacherExerciseGenerateResource {

    @Inject
    EntityManager em;
    @Inject
    JlamaService jlamaService;
    @Inject
    ExerciseCompiler exerciseCompiler;

    @POST
    public Exercise generate(Exercise exercise) throws IOException {
        Objects.requireNonNull(exercise);
        Exercise finalExercise = null;
        while (finalExercise == null) {
            finalExercise = compile(exercise.getDescription());
        }
        return finalExercise;
    }

    @POST
    @Transactional
    @Path("/save")
    public Exercise save(Exercise exercise) {
        Objects.requireNonNull(exercise);
        return (exercise.getId() != null) ? em.merge(exercise) : returnPersist(exercise);
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
            var testPath = exerciseCompiler.createTemporaryFiles(generated.getUnitTests());
            var solutionPath = exerciseCompiler.createTemporaryFiles(generated.getSolution());
            if (!exerciseCompiler.compileCode(generated.getUnitTests(), testPath) || !exerciseCompiler.compileCode(generated.getSolution(), solutionPath)) {
                System.out.println("Compilation failed, Exercise regeneration");
                return null;
            }
            return generated;
        } finally {
            exerciseCompiler.cleanDirectory();
        }
    }
}