package org.acme.resource;

import jakarta.enterprise.context.ApplicationScoped;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

@ApplicationScoped
public class ExerciseCompiler {
    public void createTemporaryFiles(String unitTests, String solution) throws IOException {
        Objects.requireNonNull(unitTests);
        Objects.requireNonNull(solution);
        var currentDirectory = Paths.get("").toAbsolutePath();
        var path = currentDirectory.resolve("tmpDirectory");
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        var testFile = path.resolve("TmpTest.java");
        var solutionFile = path.resolve("Solution.java");
        System.out.println(unitTests);
        System.out.println(testFile);
        Files.writeString(testFile, unitTests);
        Files.writeString(solutionFile, solution);
        System.out.println("CREATING FILES...");
        System.out.println("Temp files created in: " + path.toAbsolutePath());
    }

    public boolean compileCode(String unitTests, String solution) throws IOException {
        Objects.requireNonNull(unitTests);
        Objects.requireNonNull(solution);
        return true;
    }

    public boolean applyTests(String unitTests, String solution) throws IOException {
        Objects.requireNonNull(unitTests);
        Objects.requireNonNull(solution);
        return true;
    }

}
