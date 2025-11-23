package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.model.Exercise;

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

    public boolean compile(Exercise generated) {
        Objects.requireNonNull(generated);
        try {
            createTemporaryDirectory();
            var testPath = createTemporaryFiles(generated.getUnitTests());
            var solutionPath = createTemporaryFiles(generated.getSolution());
            if (!compileCode(testPath) || !compileCode(solutionPath)) {
                System.out.println("Compilation failed, Exercise regeneration");
                return false;
            }
            return true;
        } finally {
            cleanDirectory();
        }
    }

    public void createTemporaryDirectory() {
        try {
            Files.createDirectories(tmpDirectory);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    public Path createTemporaryFiles(String program) {
        Objects.requireNonNull(program);
        var fileDirectory = tmpDirectory.resolve(exerciseParser.getClassName(program));
        try {
            Files.writeString(fileDirectory, program);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        return fileDirectory;
    }

    public boolean compileCode(Path directory) {
        Objects.requireNonNull(directory);
        var compiler = ToolProvider.getSystemJavaCompiler();
        var result = compiler.run(null, null, null, directory.toString());
        System.out.println(result);
        return result == 0;
    }

    public void cleanDirectory() {
        var directory = tmpDirectory.toFile();
        var files = directory.listFiles();
        if (directory.exists()) {
            try {
                if (files != null) {
                    for (var file : files) {
                        Files.deleteIfExists(file.toPath());
                    }
                }
                Files.deleteIfExists(tmpDirectory);
            } catch (IOException e) {
                throw new AssertionError(e);
            }
        }
    }

}
