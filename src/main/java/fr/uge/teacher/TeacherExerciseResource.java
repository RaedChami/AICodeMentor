package fr.uge.teacher;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import fr.uge.exercise.dto.ExerciseDTO;
import fr.uge.exercise.ExerciseMapper;
import fr.uge.exercise.dto.UserPrompt;
import fr.uge.exercise.exception.ExerciseGenerationException;
import fr.uge.teacher.service.TeacherExerciseService;
import org.jboss.resteasy.reactive.RestPath;

@Path("/api/teacher/exercises")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TeacherExerciseResource {

    private final TeacherExerciseService teacherExerciseService;
    @Inject
    TeacherExerciseResource(TeacherExerciseService teacherExerciseService) {
        this.teacherExerciseService = Objects.requireNonNull(teacherExerciseService);
    }

    /**
     * @return all existing exercises
     */
    @Path("/")
    @GET
    public List<ExerciseDTO> getall() {
        return teacherExerciseService.getAllExercises()
                                    .stream()
                                    .map(ExerciseMapper::convertToDTO)
                                    .collect(Collectors.toList());
    }

    /**
     * @return an existing exercise
     */
    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ExerciseDTO get(@RestPath("id") long id) {
        return ExerciseMapper.convertToDTO(teacherExerciseService.getExercise(id));
    }

    /**
     * Modifies an existing exercise
     * @param id ID of an exercise
     * @param prompt Prompt from user
     * @return a Modified exercise
     * @throws ExerciseGenerationException If generation failed more than 5 times
     * @throws IOException Propagated exception from compiling class
     */
    @Path("/{id}/modify")
    @PUT
    public ExerciseDTO modify(@RestPath("id") long id, UserPrompt prompt) throws ExerciseGenerationException, IOException {
        var modified = teacherExerciseService.modifyExerciseById(id, prompt);
        return ExerciseMapper.convertToDTO(modified);
    }

    /**
     * Deletes an existing exercise
     * @param id ID of an exercise
     */
    @Path("/{id}")
    @DELETE
    public void delete(@RestPath("id") long id) {
        teacherExerciseService.deleteExercise(id);
    }

}
