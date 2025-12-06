package org.acme.student;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.exercise.Exercise;
import org.acme.exercise.service.ExerciseRunJUnitTest;
import org.jboss.resteasy.reactive.RestPath;

import java.io.IOException;
import java.util.Objects;

@Path("/api/student/exercises")
public class StudentSubmissionResource {

    private final ExerciseRunJUnitTest exerciseRunJUnitTest;
    private final EntityManager entityManager;
    @Inject
    StudentSubmissionResource(ExerciseRunJUnitTest exerciseRunJUnitTest, EntityManager entityManager) {
        this.exerciseRunJUnitTest = Objects.requireNonNull(exerciseRunJUnitTest);
        this.entityManager = Objects.requireNonNull(entityManager);
    }

    @POST
    @Path("/run-tests/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response runTests(@RestPath("id") long id, CompileRequest request) throws IOException {

        Exercise exercise = entityManager.find(Exercise.class, id);

        String result = exerciseRunJUnitTest.runUnitTests(
                request.code,
                exercise.getUnitTests()
        );

        return Response.ok(result).build();
    }

    public static class CompileRequest {
        public String code;
    }
}
