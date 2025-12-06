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
import org.acme.exercise.dto.UserPrompt;
import org.acme.exercise.service.ExerciseCompiler;

import java.io.IOException;
import java.util.Objects;

@Path("/api/teacher/generate")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TeacherExerciseGenerateResource {

    private final EntityManager entityManager;
    private final LlamaService llamaService;
    private final ExerciseCompiler exerciseCompiler;
    @Inject
    TeacherExerciseGenerateResource(EntityManager entityManager, LlamaService llamaService, ExerciseCompiler exerciseCompiler) {
        this.entityManager = Objects.requireNonNull(entityManager);
        this.llamaService = Objects.requireNonNull(llamaService);
        this.exerciseCompiler = Objects.requireNonNull(exerciseCompiler);
    }

    /**
     *
     * @param prompt Entered by the user on the web
     * @return DTO of the generated exercise
     * @throws ExerciseGenerationException if generation failed more than 10 times
     */
    @POST
    public ExerciseDTO generate(UserPrompt prompt) throws ExerciseGenerationException, IOException {
        Objects.requireNonNull(prompt);
        int attempts = 0;
        var finalExercise = llamaService.generateExercise(prompt.prompt());
        while (attempts <= 100) {
            if (finalExercise.isEmpty()) { // Format error in LLM answer, so regenerate exercise
                finalExercise = llamaService.generateExercise(prompt.prompt());
                attempts++;
                System.out.println("GENERATION FAILED");
                continue;
            }
            var exercise = finalExercise.orElseThrow();
            if (exerciseCompiler.compile(exercise)) {
                return ExerciseMapper.convertToDTO(exercise);
            }
            System.out.println("COMPILATION FAILED");
            finalExercise = llamaService.modifyExercise(exercise,
                    "L'exercice ne compile pas. CORRIGEZ les erreurs de compilation en vous assurant que :" +
                            "1. Les imports sont corrects (notamment java.util.Arrays; si utilisé)" +
                            "2. La classe Solution est bien définie" +
                            "3. Les tests utilisent correctement la classe Solution");
            attempts++;
        }
        throw new ExerciseGenerationException("Error generating an exercise after 10 attempts.");
    }

    @POST
    @Transactional
    @Path("/save")
    public ExerciseDTO save(ExerciseDTO dtoExercise) throws NoSuchFieldException, IllegalAccessException {
        Objects.requireNonNull(dtoExercise);
        var exercise = ExerciseMapper.convertToEntity(dtoExercise);
        entityManager.persist(exercise);
        return ExerciseMapper.convertToDTO(exercise);
    }

}