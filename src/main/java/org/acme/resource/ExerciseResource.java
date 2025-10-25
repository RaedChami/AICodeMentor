package org.acme.resource;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.acme.Teacher;
import org.acme.model.Exercise;
import jakarta.persistence.EntityManager;

import java.util.List;

@Path("/api/exercises")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ExerciseResource {

    @Inject
    EntityManager em;

    @POST
    @Transactional
    public Exercise create(Exercise exercise) {
        em.persist(exercise);
        return exercise;
    }

    @GET
    public List<Exercise> get() {
        return em.createNamedQuery("Exercise.findAll", Exercise.class)
                .getResultList();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public void delete(@PathParam("id") Long id) {
        Exercise exercise = em.find(Exercise.class, id);
        if (exercise != null) {
            em.remove(exercise);
        } else {
            throw new NotFoundException("Exercise with id " + id + " not found");
        }
    }

}
