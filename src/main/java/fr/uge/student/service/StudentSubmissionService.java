package fr.uge.student.service;

import fr.uge.exercise.Exercise;
import fr.uge.exercise.service.ExerciseCompiler;
import fr.uge.exercise.service.ExerciseParser;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.NotFoundException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class StudentSubmissionService {
    private final ExerciseCompiler exerciseCompiler;
    private final ExerciseParser exerciseParser;
    private final EntityManager em;
    @Inject
    StudentSubmissionService(ExerciseCompiler exerciseCompiler, ExerciseParser exerciseParser, EntityManager em) {
        this.exerciseCompiler = Objects.requireNonNull(exerciseCompiler);
        this.exerciseParser = Objects.requireNonNull(exerciseParser);
        this.em = Objects.requireNonNull(em);
    }
    private static ProcessBuilder initProcess(String testClassName, java.nio.file.Path studentFile, java.nio.file.Path testFile) {
        Objects.requireNonNull(testClassName);
        Objects.requireNonNull(studentFile);
        Objects.requireNonNull(testFile);
        var studentParent = Objects.requireNonNull(studentFile.getParent(), "studentFile parent is null");
        var testParent = Objects.requireNonNull(testFile.getParent(), "testFile parent is null");

        String classpath =
                System.getProperty("java.class.path")
                        + ":target/classes"
                        + ":target/quarkus-app/lib/*"
                        + ":" + studentParent
                        + ":" + testParent;

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
        var result = new StringBuilder();
        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append('\n');
            }
        }
        try (BufferedReader errorReader =
                     new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8))) {
            StringBuilder errors = new StringBuilder();
            String errLine;
            while ((errLine = errorReader.readLine()) != null) {
                errors.append(errLine).append('\n');
            }
            if(!errors.isEmpty()) {
                return errors.toString();
            }
        }
        return result.toString();
    }
    public String getTestOutput(long id, String studentCode) throws IOException, InterruptedException {
        Exercise exercise = em.find(Exercise.class, id);
        if (exercise == null) {
            throw new NotFoundException("Exercise not found");
        }
        exerciseCompiler.createTemporaryDirectory();
        var studentFile = exerciseCompiler.createTemporaryFile(studentCode);
        var testFile = exerciseCompiler.createTemporaryFile(exercise.getUnitTests());

        var testClassName = exerciseParser.getClassName(exercise.getUnitTests()).replace(".java", "");

        Process process = initProcess(testClassName, studentFile, testFile).start();

        boolean finished = process.waitFor(10, TimeUnit.SECONDS);

        if (!finished) {
            process.destroyForcibly();
            exerciseCompiler.cleanDirectory();
            return "Execution du test interrompue : dépassement du temps limite";
        }

        String result = getProcessOutput(process);
        exerciseCompiler.cleanDirectory();
        return result;
    }
}
