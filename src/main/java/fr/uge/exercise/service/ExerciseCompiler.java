package fr.uge.exercise.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import fr.uge.exercise.Exercise;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.tools.ToolProvider;

@ApplicationScoped
public class ExerciseCompiler {

    private final ExerciseParser exerciseParser;
    @Inject
    ExerciseCompiler(ExerciseParser exerciseParser) {
        this.exerciseParser = Objects.requireNonNull(exerciseParser);
    }

    private final static Path tmpDirectory = Paths.get("").toAbsolutePath().resolve("tmpDirectory");
    private List<String> files = new ArrayList<>();

    /**
     * Creates the conditions for compilation of the JUnit tests
     * and the solution class of an exercise
     * @param exercise an exercise to be compiled
     * @return result of compilation
     * @throws IOException exception thrown by writing in files
     */
    public boolean compile(Exercise exercise) throws IOException {
        Objects.requireNonNull(exercise);
        try {
            var directory = createTemporaryDirectory();
            createTemporaryFiles(exercise.getUnitTests());
            createTemporaryFiles(exercise.getSolution());
            return compilePrograms(directory);
        } finally {
            files = new ArrayList<>();
            cleanDirectory();
        }
    }

    public Path createTemporaryDirectory() throws IOException {
        return Files.createDirectories(tmpDirectory);
    }

    public Path createTemporaryFiles(String program) throws IOException {
        Objects.requireNonNull(program);
        var fileDirectory = tmpDirectory.resolve(exerciseParser.getClassName(program));
        files.add(fileDirectory.toString());
        Files.writeString(fileDirectory, program);
        return fileDirectory;
    }

    /**
     * Compiles programs in a directory
     * @param directory temporary directory for compilation
     * @return true if compilation success, otherwise false
     */
    private boolean compilePrograms(Path directory) {
        Objects.requireNonNull(directory);
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
