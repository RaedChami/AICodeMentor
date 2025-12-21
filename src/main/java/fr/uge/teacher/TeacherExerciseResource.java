package fr.uge.teacher;

import fr.uge.exercise.exception.ExerciseUnauthorizedAccess;
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
     * Returns all existing exercises
     * @return a list of all exercises in DTO format
     */
    @Path("/")
    @GET
    public List<ExerciseDTO> getAll() {
        return teacherExerciseService.getAllExercises()
                                    .stream()
                                    .map(ExerciseMapper::convertToDTO)
                                    .collect(Collectors.toList());
    }

    /**
     * Get all exercises created by a specific user
     * @param userId The ID of the user/creator
     * @return List of exercises created by this user
     */
    @Path("user/{userId}")
    @GET
    public List<ExerciseDTO> getByUserId(@RestPath("userId") long userId) {
        return teacherExerciseService.getExercisesByUserId(userId)
                                    .stream()
                                    .map(ExerciseMapper::convertToDTO)
                                    .collect(Collectors.toList());
    }

    /**
     * Returns an existing exercise
     * @param id serial number of the exerise
     * @return DTO of the exercise
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
     * @throws ExerciseGenerationException thrown if generation failed more than 5 times
     * @throws ExerciseUnauthorizedAccess thrown if attempt by a user to modify an exercise created by someone else
     * @throws IOException Propagated exception from compiling class
     */
    @Path("/{id}/modify")
    @PUT
    public ExerciseDTO modify(@RestPath("id") long id, UserPrompt prompt) throws ExerciseGenerationException, ExerciseUnauthorizedAccess, IOException {
        var modified = teacherExerciseService.modifyExerciseById(id, prompt);
        return ExerciseMapper.convertToDTO(modified);
    }

    /**
     * Deletes an existing exercise
     * @param id serial number of an exercise
     * @param userId ID of a user
     */
    @Path("/{id}")
    @DELETE
    public void delete(@RestPath("id") long id, @QueryParam("userId") long userId) {
        teacherExerciseService.deleteExercise(id, userId);
    }

}
