package fr.uge.student;

import fr.uge.student.service.StudentSubmissionService;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import fr.uge.exercise.Exercise;
import fr.uge.exercise.service.ExerciseCompiler;
import fr.uge.exercise.service.ExerciseParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Path("/api/student/exercises")
public class StudentSubmissionResource {

    private final StudentSubmissionService studentSubmissionService;

    @Inject
    StudentSubmissionResource(StudentSubmissionService studentSubmissionService) {
        this.studentSubmissionService = studentSubmissionService;
    }

    /**
     * Call getTestOuput method which launch the Junit test on student Code
     * @param id
     * @param request
     * @return the result of the Junit test
     * @throws IOException
     * @throws InterruptedException
     */
    @POST
    @Path("/run-tests/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response runTests(@PathParam("id") long id, CompileRequest request) throws IOException, InterruptedException {
        try {
            var output = studentSubmissionService.getTestOutput(id, request.getCode());
            return Response.ok(output).build();
        }
        catch(NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Exercise not found")
                    .build();
        }
    }
    public static class CompileRequest {
        private String code;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }
}