package org.acme.teacher;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.acme.exercise.dto.ExerciseDTO;
import org.acme.exercise.exception.ExerciseGenerationException;
import org.acme.llm.LlamaService;
import org.acme.exercise.ExerciseMapper;
import org.acme.exercise.Exercise;
import org.acme.exercise.dto.UserPrompt;
import org.acme.exercise.ExerciseCompiler;

import java.io.IOException;
import java.util.Objects;

@Path("/api/teacher/generate")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TeacherExerciseGenerateResource {

    @Inject
    EntityManager em;
    @Inject
    LlamaService llamaService;
    @Inject
    ExerciseCompiler exerciseCompiler;

    @POST
    public ExerciseDTO generate(UserPrompt prompt) throws IOException {
        Objects.requireNonNull(prompt);
        Exercise finalExercise = null;
        int attempts = 0;
        while (attempts <= 100) {
            finalExercise = llamaService.generateExercise(prompt.prompt());
            if (finalExercise != null && exerciseCompiler.compile(finalExercise.getUnitTests())
                                    && exerciseCompiler.compile(finalExercise.getSolution())) {
                return ExerciseMapper.convertToDTO(finalExercise);
            }
            attempts++;
        }
        throw new ExerciseGenerationException("Impossible de générer un exercise valide après 5 tentatives");
    }

    @POST
    @Transactional
    @Path("/save")
    public ExerciseDTO save(ExerciseDTO dtoExercise) {
        Objects.requireNonNull(dtoExercise);
        var exercise = ExerciseMapper.convertToEntity(dtoExercise);
        em.persist(exercise);
        return ExerciseMapper.convertToDTO(exercise);
    }

}