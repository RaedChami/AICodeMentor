package fr.uge.student;

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
import java.util.Objects;

@Path("/api/student/exercises")
public class StudentSubmissionResource {

    private final ExerciseCompiler exerciseCompiler;
    private final ExerciseParser exerciseParser;
    private final EntityManager em;
    @Inject
    StudentSubmissionResource(ExerciseCompiler exerciseCompiler, ExerciseParser exerciseParser, EntityManager em) {
        this.exerciseCompiler = Objects.requireNonNull(exerciseCompiler);
        this.exerciseParser = Objects.requireNonNull(exerciseParser);
        this.em = Objects.requireNonNull(em);
    }
    private static ProcessBuilder initProcess(String testClassName, java.nio.file.Path studentFile, java.nio.file.Path testFile) {
        Objects.requireNonNull(testClassName);
        Objects.requireNonNull(studentFile);
        Objects.requireNonNull(testFile);
        String classpath =
                System.getProperty("java.class.path")
                        + ":" + "target/classes"
                        + ":" + "target/quarkus-app/lib/*"
                        + ":" + studentFile.getParent().toString()
                        + ":" + testFile.getParent().toString();
        return new ProcessBuilder(
                "java",
                "-cp", classpath,
                "fr.uge.student.SubProcessMain",
                testClassName,
                studentFile.toString(),
                testFile.toString()
        );
    }

    private static String getProcessOutput(Process process) throws IOException {
        Objects.requireNonNull(process);
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(process.getInputStream()));
        var result = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line).append('\n');
        }
        BufferedReader errorReader =
                new BufferedReader(new InputStreamReader(process.getErrorStream()));
        StringBuilder errors = new StringBuilder();
        String errLine;
        while ((errLine = errorReader.readLine()) != null) {
            errors.append(errLine).append('\n');
        }
        if(!errors.isEmpty()) {
            return errors.toString();
        }
        return result.toString();
    }

    @POST
    @Path("/run-tests/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response runTests(@PathParam("id") long id, CompileRequest request) throws IOException, InterruptedException {
        Exercise exercise = em.find(Exercise.class, id);
        exerciseCompiler.createTemporaryDirectory();

        var studentFile = exerciseCompiler.createTemporaryFile(request.code);
        var testFile = exerciseCompiler.createTemporaryFile(exercise.getUnitTests());

        var testClassName = exerciseParser.getClassName(exercise.getUnitTests()).replace(".java", "");

        Process process = initProcess(testClassName, studentFile, testFile).start();
        process.waitFor();

        String result = getProcessOutput(process);
        exerciseCompiler.cleanDirectory();
        return Response.ok(result).build();
    }
    public static class CompileRequest {
        public String code;
    }
}
