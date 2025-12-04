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
import java.util.Optional;

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

    /**
     *
     * @param prompt Entered by the user on the web
     * @return DTO of the generated exercise
     * @throws ExerciseGenerationException if generation failed more than 10 times
     */
    @POST
    public ExerciseDTO generate(UserPrompt prompt) throws ExerciseGenerationException {
        Objects.requireNonNull(prompt);
        int attempts = 0;
        var finalExercise = llamaService.generateExercise(prompt.prompt());
        while (attempts <= 100) {
            if (finalExercise.isEmpty()) {
                finalExercise = llamaService.generateExercise(prompt.prompt());
                attempts++;
                continue;
            }
            var exercise = finalExercise.orElseThrow();
            try {
                if (exerciseCompiler.compile(exercise)) {
                    return ExerciseMapper.convertToDTO(exercise);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            finalExercise = llamaService.modifyExercise(exercise,
                    "L'exercice ne compile pas. Corrigez les erreurs de compilation en vous assurant que :\n" +
                            "1. Les imports sont corrects (notamment java.util.Arrays; si utilisé)\n" +
                            "2. La classe Solution est bien définie\n" +
                            "3. Les tests utilisent correctement la classe Solution\n");
            attempts++;
        }
        throw new ExerciseGenerationException("Error generating an exercise after 10 attempts.");
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