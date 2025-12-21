package fr.uge.teacher;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import fr.uge.exercise.dto.ExerciseDTO;
import fr.uge.exercise.exception.ExerciseGenerationException;
import fr.uge.exercise.ExerciseMapper;
import fr.uge.exercise.dto.UserPrompt;
import fr.uge.teacher.service.TeacherGenerateService;

import java.io.IOException;
import java.util.Objects;

@Path("/api/teacher/generate")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TeacherExerciseGenerateResource {

    private final TeacherGenerateService teacherGenerateService;
    @Inject
    TeacherExerciseGenerateResource(TeacherGenerateService teacherGenerateService) {
        this.teacherGenerateService = Objects.requireNonNull(teacherGenerateService);
    }

    /**
     * Exercise generation endpoint
     * @param prompt Entered by the user on the web
     * @return DTO of the generated exercise
     * @throws ExerciseGenerationException if generation failed more than 10 times
     */
    @POST
    public ExerciseDTO generate(UserPrompt prompt) throws ExerciseGenerationException, IOException {
        return ExerciseMapper.convertToDTO(teacherGenerateService.generateExerciseService(prompt));
    }

    /**
     * Exercise saving endpoint
     * @param dtoExercise DTO of the generated exercise
     * @return DTO of the generated exercise
     * @throws NoSuchFieldException propagated exception from mappers
     * @throws IllegalAccessException propagated exception from mappers
     */
    @POST
    @Path("/save")
    public ExerciseDTO save(ExerciseDTO dtoExercise) throws NoSuchFieldException, IllegalAccessException {
        return ExerciseMapper.convertToDTO(teacherGenerateService.saveGeneratedExercise(dtoExercise));
    }

}