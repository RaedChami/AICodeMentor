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
     * Creates the conditions for the compilation of the JUnit tests
     * and the solution class of an exercise.
     * @param exercise an exercise to be compiled
     * @return result of the compilation then cleanup of the temporary directory and its content
     * @throws IOException thrown from writing the files for the compilation
     */
    public boolean compile(Exercise exercise) throws IOException {
        Objects.requireNonNull(exercise);
        try {
            var directory = createTemporaryDirectory();
            createTemporaryFile(exercise.getUnitTests());
            createTemporaryFile(exercise.getSolution());
            return compilePrograms(directory);
        } finally {
            files = new ArrayList<>();
            cleanDirectory();
        }
    }

    /**
     * Creates a temporary directory
     * @return The path leading to the directory
     * @throws IOException
     */
    public Path createTemporaryDirectory() throws IOException {
        return Files.createDirectories(tmpDirectory);
    }

    /**
     * Creates a temporary file containing a program
     * @param program Java class to be compiled
     * @return The directory path leading to the file
     * @throws IOException
     */
    public Path createTemporaryFile(String program) throws IOException {
        Objects.requireNonNull(program);
        var fileDirectory = tmpDirectory.resolve(exerciseParser.getClassName(program));
        files.add(fileDirectory.toString());
        Files.writeString(fileDirectory, program);
        return fileDirectory;
    }

    /**
     * Compiles java files that are in a directory
     * @param directory temporary directory for compilation
     * @return true if compilation success, otherwise false
     */
    private boolean compilePrograms(Path directory) {
        Objects.requireNonNull(directory);
        var compiler = ToolProvider.getSystemJavaCompiler();
        var result = compiler.run(null, null, null, files.toArray(String[]::new));
        return result == 0;
    }

    /**
     * Cleans up the temporary directory and its content
     * @throws IOException
     */
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
