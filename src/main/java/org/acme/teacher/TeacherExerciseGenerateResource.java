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
import org.acme.teacher.service.TeacherGenerateService;

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
     *
     * @param prompt Entered by the user on the web
     * @return DTO of the generated exercise
     * @throws ExerciseGenerationException if generation failed more than 10 times
     */
    @POST
    public ExerciseDTO generate(UserPrompt prompt) throws ExerciseGenerationException, IOException {
        return ExerciseMapper.convertToDTO(teacherGenerateService.generateExerciseService(prompt));
    }

    @POST
    @Path("/save")
    public ExerciseDTO save(ExerciseDTO dtoExercise) throws NoSuchFieldException, IllegalAccessException {
        return ExerciseMapper.convertToDTO(teacherGenerateService.saveGeneratedExercise(dtoExercise));
    }

}