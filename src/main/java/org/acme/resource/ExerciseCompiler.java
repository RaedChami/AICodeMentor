package org.acme.resource;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import javax.tools.JavaCompiler;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import javax.tools.ToolProvider;

@ApplicationScoped
public class ExerciseCompiler {

    @Inject
    ExerciseParser exerciseParser;

    private final static Path tmpDirectory = Paths.get("").toAbsolutePath().resolve("tmpDirectory");
    private Path testDirectory;
    private Path solutionDirectory;
    public void createTemporaryFiles(String unitTests, String solution) throws IOException {
        Objects.requireNonNull(unitTests);
        Objects.requireNonNull(solution);;
        try {
            Files.createDirectories(tmpDirectory);
        } catch (IOException e) {
            System.out.println("Directory creation failed: " + e.getMessage());
            throw new AssertionError(e);
        }
        testDirectory = tmpDirectory.resolve(exerciseParser.getClassName(unitTests));
        solutionDirectory = tmpDirectory.resolve(exerciseParser.getClassName(solution));
        try {
            Files.writeString(testDirectory, unitTests);
            Files.writeString(solutionDirectory, solution);
            System.out.println("CREATING FILES...");
            System.out.println("Temp files created in: " + tmpDirectory.toAbsolutePath());
        } catch (IOException e) {
            System.out.println("File creation failed: " + e.getMessage());
            throw new AssertionError(e);
        }
    }

    public boolean compileCode(String unitTests, String solution) {
        Objects.requireNonNull(unitTests);
        Objects.requireNonNull(solution);
        var compiler = ToolProvider.getSystemJavaCompiler();
        var result = compiler.run(null, null, null, testDirectory.toString(), solutionDirectory.toString());
        System.out.println(result);
        return result == 0;
    }

    public boolean applyTests(String unitTests, String solution) throws IOException {
        Objects.requireNonNull(unitTests);
        Objects.requireNonNull(solution);
        return true;
    }

    public void cleanDirectory(String unitTests, String solution) throws IOException {
        Objects.requireNonNull(unitTests);
        Objects.requireNonNull(solution);
        try {
            Files.deleteIfExists(testDirectory);
            Files.deleteIfExists(solutionDirectory);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

}
