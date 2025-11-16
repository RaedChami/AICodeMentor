package org.acme.teacher;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.Objects;

import org.acme.dto.ExerciseDTO;
import org.acme.mapper.ExerciseMapper;
import org.acme.model.Exercise;

@Path("/api/teacher/exercises")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TeacherExerciseResource {

    @Inject
    EntityManager em;

    /**
     * @return
     * Returns all created exercises
     */
    @Path("/")
    @GET
    public List<Exercise> getall() {
      return em.createNamedQuery("Exercise.findAll", Exercise.class)
              .getResultList();
    }

    /**
     * @return
     * Returns one given exercise
     */
    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Exercise get(@PathParam("id") long id) {
        if (id < 0) {
            throw new IllegalArgumentException("exercise ID is < 0");
        }
        return em.find(Exercise.class, id);
    }

    @PUT
    @Transactional
    @Path("/{id}")
    public ExerciseDTO update(@PathParam("id") long id, ExerciseDTO dtoExercise) {
        Objects.requireNonNull(dtoExercise);
        if (id < 0) {
            throw new IllegalArgumentException("exercise ID is < 0");
        }
        var findExercise = em.find(Exercise.class, id);
        if (findExercise == null) {
            throw new IllegalArgumentException("exercise ID doesn't exist");
        }
        var dtoWithId = new ExerciseDTO(
                id,
                dtoExercise.description(),
                dtoExercise.difficulty(),
                dtoExercise.concepts(),
                dtoExercise.signatureAndBody(),
                dtoExercise.unitTests(),
                dtoExercise.solution()
        );
        var exercise = ExerciseMapper.convertToEntity(dtoWithId);
        exercise = em.merge(exercise);
        return ExerciseMapper.convertToDTO(exercise);
    }

    @Path("/{id}")
    @DELETE
    @Transactional
    public void delete(@PathParam("id") long id) {
        if (id < 0) {
            throw new IllegalArgumentException("exercise ID is < 0");
        }
        Exercise exercise = em.find(Exercise.class, id);
        if (exercise != null) {
            em.remove(exercise);
        } else {
            throw new NotFoundException("Exercise with id " + id + " not found");
        }
    }

}
