package org.acme.exercise;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.tools.ToolProvider;

@ApplicationScoped
public class ExerciseCompiler {

    @Inject
    ExerciseParser exerciseParser;

    private final static Path tmpDirectory = Paths.get("").toAbsolutePath().resolve("tmpDirectory");
    private final List<String> files = new ArrayList<>();

    public boolean compile(Exercise exercise) throws IOException {
        Objects.requireNonNull(exercise);
        try {
            var directory = createTemporaryDirectory();
            createTemporaryFiles(exercise.getUnitTests());
            createTemporaryFiles(exercise.getSolution());
            return compilePrograms(directory);
        } finally {
            files.clear();
            cleanDirectory();
        }
    }

    public Path createTemporaryDirectory() throws IOException {
        return Files.createDirectories(tmpDirectory);
    }

    public void createTemporaryFiles(String program) throws IOException {
        Objects.requireNonNull(program);
        var fileDirectory = tmpDirectory.resolve(exerciseParser.getClassName(program));
        files.add(fileDirectory.toString());
        Files.writeString(fileDirectory, program);
    }

    public boolean compilePrograms(Path directory) {
        Objects.requireNonNull(directory);
        System.out.println("ARGUMENTS " + files);
        var compiler = ToolProvider.getSystemJavaCompiler();
        var result = compiler.run(null, null, null, files.toArray(String[]::new));
        System.out.println(result);
        return result == 0;
    }

    public void cleanDirectory() throws IOException {
        var directory = tmpDirectory.toFile();
        var files = directory.listFiles();
        if (directory.exists()) {
            if (files != null) {
                for (var file : files) {
                    Files.deleteIfExists(file.toPath());
                }
            }
            Files.deleteIfExists(tmpDirectory);
        }
    }
}
