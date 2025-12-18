package fr.uge.exercise.service;

import fr.uge.exercise.Difficulty;
import fr.uge.exercise.Exercise;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class ExerciseCompilerTest {

    private final String validSolution = """
            import java.util.*;
            public class Sum {
                public int add(int a, int b) {
                    return a + b;
                }
            }
            """;

    private final String validTest = """
            import org.junit.jupiter.api.Test;
            import static org.junit.jupiter.api.Assertions.*;
            import java.util.*;
            
            public class SumTest {
                @Test
                void testAdd() {
                    var sumFunc = new Sum();
                    assertEquals(5, sumFunc.add(2, 3));
                }
            }
            """;

    private final String invalidSolution = """
            public class Sum {
                public int add(int a, int b) {
                    return a + b  // Missing semicolon
                }
            }
            """;

    private final String invalidSolutionClassName = """
            public class Sam { // Invalid class name
                public int add(int a, int b) {
                    return a + b;
                }
            }
            """;

    @Test
    @DisplayName("constructor parameter is not null")
    public void shouldThrowNullIfParserIsNull() {
        assertThrows(NullPointerException.class, () -> {
            new ExerciseCompiler(null);
        });
    }

    @Test
    @DisplayName("compile method parameter is not null")
    public void shouldThrowNullIfExerciseIsNull() {
        var parser = new ExerciseParser();
        var compiler = new ExerciseCompiler(parser);
        assertThrows(NullPointerException.class, () -> {
            compiler.compile(null);
        });
    }

    @Test
    @DisplayName("compile method returns 0 if tested program is valid")
    public void compilerMethodSuccessfulIfProgramValid() throws IOException {
        var parser = new ExerciseParser();
        var compiler = new ExerciseCompiler(parser);
        var result = compiler.compile(
                new Exercise("*description*",
                                Difficulty.L1,
                                List.of("*concept*"),
                                "*signature*",
                                validTest,
                                validSolution
                        ));
        assertTrue(result, "Valid code should compile successfully");
    }

    @Test
    @DisplayName("compile method returns 1 if tested program is invalid")
    public void compilerMethodFailsIfProgramInvalid() throws IOException {
        var parser = new ExerciseParser();
        var compiler = new ExerciseCompiler(parser);
        var test1 = compiler.compile(
                new Exercise("*description*",
                        Difficulty.L1,
                        List.of("*concept*"),
                        "*signature*",
                        validTest,
                        invalidSolution
                ));
        var test2 = compiler.compile(
                new Exercise("*description*",
                        Difficulty.L1,
                        List.of("*concept*"),
                        "*signature*",
                        validTest,
                        invalidSolutionClassName
                ));
        assertFalse(test1, "Invalid code should fail");
        assertFalse(test2, "Invalid code should fail");
    }

    @Test
    @DisplayName("temporary directory is created")
    public void shouldCreateTemporaryDirectory() throws IOException {
        var parser = new ExerciseParser();
        var compiler = new ExerciseCompiler(parser);
        var directory = compiler.createTemporaryDirectory();
        assertNotNull(directory);
        assertTrue(Files.exists(directory), "Directory should exist");
        assertTrue(Files.isDirectory(directory), "Path should be a directory");
        compiler.cleanDirectory();
    }

    @Test
    @DisplayName("temporary directory is cleaned")
    public void shouldCleanTemporaryDirectory() throws IOException {
        var parser = new ExerciseParser();
        var compiler = new ExerciseCompiler(parser);
        var directory = compiler.createTemporaryDirectory();
        assertNotNull(directory);
        compiler.cleanDirectory();
        assertTrue(Files.notExists(directory), "Directory should not exist");
    }

    @Test
    @DisplayName("temporary file is created")
    public void shouldCreateTemporaryFileForCompilation() throws IOException {
        var parser = new ExerciseParser();
        var compiler = new ExerciseCompiler(parser);
        compiler.createTemporaryDirectory();
        var filePath = compiler.createTemporaryFile(validSolution);
        assertNotNull(filePath);
        assertTrue(Files.exists(filePath), "File should exist");
        var content = Files.readString(filePath);
        assertEquals(validSolution, content, "File content should match");
        compiler.cleanDirectory();
    }

}