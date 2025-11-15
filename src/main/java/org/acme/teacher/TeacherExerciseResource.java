package org.acme.teacher;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

import org.acme.model.Exercise;

@Path("/api/teacher/exercises")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TeacherExerciseResource {

    @Inject
    EntityManager em;

    @Path("/")
    @GET
    public List<Exercise> get() {
      return em.createNamedQuery("Exercise.findAll", Exercise.class)
              .getResultList();
    }

    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Exercise getById(@PathParam("id") long id) {
        if (id < 0) {
            throw new IllegalArgumentException("exercise ID is < 0");
        }
        return em.find(Exercise.class, id);
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
