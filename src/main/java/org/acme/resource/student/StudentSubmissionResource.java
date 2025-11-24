package org.acme.resource.student;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.model.Exercise;
import org.acme.service.ExerciseCompiler;

@Path("/api/student/exercises")
public class StudentSubmissionResource {

    @Inject
    ExerciseCompiler exerciseCompiler;

    @Inject
    EntityManager em;

    @POST
    @Path("/run-tests/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response runTests(@PathParam("id") long id, CompileRequest request) {

        Exercise exercise = em.find(Exercise.class, id);

        String result = exerciseCompiler.runUnitTests(
                request.code,
                exercise.getUnitTests()
        );

        return Response.ok(result).build();
    }

    public static class CompileRequest {
        public String code;
    }
}
