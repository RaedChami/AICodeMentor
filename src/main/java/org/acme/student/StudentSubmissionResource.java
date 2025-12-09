package org.acme.student;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.exercise.Exercise;
import org.acme.exercise.service.ExerciseCompiler;
import org.acme.exercise.service.ExerciseParser;
import org.acme.exercise.service.ExerciseRunJUnitTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Path("/api/student/exercises")
public class StudentSubmissionResource {

    @Inject
    ExerciseCompiler exerciseCompiler;

    @Inject
    ExerciseParser exerciseParser;

    @Inject
    EntityManager em;

    private ProcessBuilder initProcess(String testClassName, java.nio.file.Path studentFile, java.nio.file.Path testFile) {
        String classpath = System.getProperty("java.class.path");
        String outputDir = "target/classes";

        ProcessBuilder pb = new ProcessBuilder(
                "java",
                "-cp", classpath + ":" + outputDir,
                "org.acme.student.SubProcessMain",
                testClassName,
                studentFile.toString(),
                testFile.toString()
        );
        return pb;
    }

    @POST
    @Path("/run-tests/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response runTests(@PathParam("id") long id, CompileRequest request) throws IOException, InterruptedException {
        Exercise exercise = em.find(Exercise.class, id);
        exerciseCompiler.createTemporaryDirectory();

        java.nio.file.Path studentFile = exerciseCompiler.createTemporaryFiles(request.code);
        java.nio.file.Path testFile = exerciseCompiler.createTemporaryFiles(exercise.getUnitTests());

        var testClassName = exerciseParser.getClassName(exercise.getUnitTests()).replace(".java", "");

        Process process = initProcess(testClassName, studentFile, testFile).start();
        process.waitFor();

        BufferedReader reader =
                new BufferedReader(new InputStreamReader(process.getInputStream()));
        var result = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line).append('\n');
        }
        return Response.ok(result).build();
    }

    public static class CompileRequest {
        public String code;
    }
}
