package fr.uge.teacher.service;

import fr.uge.login.Login;
import fr.uge.login.LoginMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import fr.uge.exercise.Exercise;
import fr.uge.exercise.ExerciseMapper;
import fr.uge.exercise.dto.ExerciseDTO;
import fr.uge.exercise.dto.UserPrompt;
import fr.uge.exercise.exception.ExerciseGenerationException;
import fr.uge.exercise.service.ExerciseCompiler;
import fr.uge.llm.LlamaService;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
public class TeacherGenerateService {

    private final static int MAX_ATTEMPTS = 10;
    private final EntityManager entityManager;
    private final LlamaService llamaService;
    private final ExerciseCompiler exerciseCompiler;

    @Inject
    TeacherGenerateService(EntityManager entityManager, LlamaService llamaService, ExerciseCompiler exerciseCompiler) {
        this.entityManager = Objects.requireNonNull(entityManager);
        this.llamaService = Objects.requireNonNull(llamaService);
        this.exerciseCompiler = Objects.requireNonNull(exerciseCompiler);
    }

    /**
     * Returns an AI-generated exercise corresponding to the requested prompt
     * and fails after a specific number of attempts of generation
     * @param prompt the requested prompt sent from the user
     * @return A complete exercise if success of generation
     * @throws ExerciseGenerationException Lifted if generation failed after a specific number of attempts
     * @throws IOException Propagated exception from writing the files for the compilation
     */
    public Exercise generateExerciseService(UserPrompt prompt) throws ExerciseGenerationException, IOException {
        Objects.requireNonNull(prompt);
        var generatedExercise = llamaService.generateExercise(prompt.prompt());
        var teacher = entityManager.find(Login.class, prompt.creatorId());

        for (int i = 0; i < MAX_ATTEMPTS; i++) { // bounded generation loop
            if (generatedExercise.isPresent()) { // generation output is valid
                var exercise = generatedExercise.orElseThrow();
                exercise.setCreator(teacher);
                if (exerciseCompiler.compile(exercise)) { // compilation check for generated programs
                    return exercise;
                }
                generatedExercise = llamaService.modifyExercise(exercise, // regenerate with modification to resolve compilation
                        "L'exercice ne compile pas. CORRIGEZ les erreurs de compilation.");
            } else { // generation output is unvalid
                generatedExercise = regenerateExercise(prompt);
            }
        }
        throw new ExerciseGenerationException("Impossible de générer un exercice valide après 10 tentatives.");
    }

    /**
     * Reusable generation of exercise method
     * @param prompt the requested prompt sent from the user
     * @return A generated exercise
     */
    private Optional<Exercise> regenerateExercise(UserPrompt prompt) {
        return llamaService.generateExercise(prompt.prompt());
    }

    /**
     * Saves into DB a newly generated exercise
     * @param dtoExercise DTO of the generated exercise
     * @return entity of the saved exercise
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    @Transactional
    public Exercise saveGeneratedExercise(ExerciseDTO dtoExercise) throws NoSuchFieldException, IllegalAccessException {
        Objects.requireNonNull(dtoExercise);
        var exercise = ExerciseMapper.convertToEntity(dtoExercise);
        exercise.setCreator(LoginMapper.convertToEntity(dtoExercise.creator())); // set the author before saving exercise
        entityManager.persist(exercise);
        return exercise;
    }
}
