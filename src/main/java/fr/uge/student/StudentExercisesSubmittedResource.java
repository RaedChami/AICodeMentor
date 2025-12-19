package fr.uge.student;

import fr.uge.exercise.Exercise;
import fr.uge.exercise.ExerciseMapper;
import fr.uge.exercise.ExerciseSubmitted;
import fr.uge.exercise.dto.ExerciseDTO;
import fr.uge.exercise.dto.ExerciseSubmittedDTO;
import fr.uge.login.Login;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.Objects;

@Path("/api/student/exercises-submitted")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StudentExercisesSubmittedResource {

    private final EntityManager em;
    @Inject
    StudentExercisesSubmittedResource(EntityManager entityManager) {
        this.em = entityManager;
    }

    /**
     * @return all exercises submitted by the student
     */
    @GET
    public List<ExerciseDTO> findExercisesByLoginId(@QueryParam("loginId") Long loginId) {
        Objects.requireNonNull(loginId);

        List<Exercise> exercises = em.createNamedQuery(
                        "ExerciseSubmitted.findExercisesByLoginId",
                        Exercise.class   // ✅ BON TYPE
                )
                .setParameter("loginId", loginId)
                .getResultList();

        return exercises.stream()
                .map(ExerciseMapper::convertToDTO)
                .toList();
    }

    @POST
    @Transactional
    public void submitExercise(ExerciseSubmittedDTO dto) {
        Objects.requireNonNull(dto);

        Login login = em.find(Login.class, dto.loginId());

        if (login == null) {
            throw new NotFoundException("Login not found");
        }

        Exercise exercise = em.find(Exercise.class, dto.exerciseId());
        if (exercise == null) {
            throw new NotFoundException("Exercise not found");
        }

        ExerciseSubmitted submission = new ExerciseSubmitted(
                login,
                exercise,
                dto.solutionSubmitted()
        );
        em.persist(submission);
    }
    @GET
    @Path("/solution")
    public String getSubmittedSolution(
            @QueryParam("loginId") Long loginId,
            @QueryParam("exerciseId") Long exerciseId
    ) {
        Objects.requireNonNull(loginId);
        Objects.requireNonNull(exerciseId);

        List<ExerciseSubmitted> result = em.createNamedQuery(
                        "ExerciseSubmitted.findByLoginAndExercise",
                        ExerciseSubmitted.class
                )
                .setParameter("loginId", loginId)
                .setParameter("exerciseId", exerciseId)
                .getResultList();

        if (result.isEmpty()) {
            return "";
        }

        return result.get(0).getSolutionSubmitted();
    }

}

