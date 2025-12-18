package fr.uge.exercise.service;

import fr.uge.exercise.Difficulty;
import fr.uge.exercise.Exercise;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class ExerciseParserTest {
    private final static Pattern classPattern = Pattern.compile("class (.*)\\{");
    private final static Pattern enoncePattern = Pattern.compile("<ENONCE>(.*)</ENONCE>", Pattern.DOTALL);
    private final static Pattern difficultyPattern = Pattern.compile("<DIFFICULTE>(.*)</DIFFICULTE>", Pattern.DOTALL);
    private final static Pattern conceptsPattern = Pattern.compile("<CONCEPTS>(.*)</CONCEPTS>", Pattern.DOTALL);
    private final static Pattern signatureBodyPattern = Pattern.compile("<SIGNATURE>(.*)</SIGNATURE>", Pattern.DOTALL);
    private final static Pattern testsPattern = Pattern.compile("<TESTS>(.*)</TESTS>", Pattern.DOTALL);
    private final static Pattern solutionPattern = Pattern.compile("<SOLUTION>(.*)</SOLUTION>",
            Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

    private final String validFullContent = """
                <ENONCE>
                *description*
                </ENONCE>
                <DIFFICULTE>
                L1
                </DIFFICULTE>
                <CONCEPTS>
                concept1, concept2
                </CONCEPTS>
                <SIGNATURE>
                public class Solution {
                    public String printHelloWorld() {
                        // TODO
                    }
                }
                </SIGNATURE>
                <TESTS>
                import java.util.*;
                public class SolutionTest {
                    @Test
                    public void test1() {
                        Solution solution = new Solution();
                    }
                }
                </TESTS>
                <SOLUTION>
                import java.util.*;
                public class Solution {
                }
                </SOLUTION>
                """;

    @Test
    @DisplayName("should throw NullPointerException when answer is null")
    public void shouldThrowNullPointerExceptionWhenAnswerIsNull() {
        var parser = new ExerciseParser();
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    @DisplayName("class name should be parsed and return the file that holds the content of the class")
    public void shouldParseValidClassName() {
        var classExample = """
            import java.util.*;
            public class Sum {
                public int add(int a, int b) {
                    return a + b;
                }
            }
            """;
        var parser = new ExerciseParser();
        assertNotNull(parser.getClassName(classExample));
        assertEquals("Sum.java", parser.getClassName(classExample));
    }

    @Test
    @DisplayName("should throw when class pattern not found in getClassName")
    public void shouldThrowWhenClassPatternNotFound() {
        var invalidCode = "not a class definition";
        var parser = new ExerciseParser();
        assertThrows(Exception.class, () -> parser.getClassName(invalidCode));
    }

    @Test
    @DisplayName("should throw NullPointerException when code is null in getClassName")
    public void shouldThrowNullPointerExceptionWhenCodeIsNull() {
        var parser = new ExerciseParser();
        assertThrows(NullPointerException.class, () -> parser.getClassName(null));
    }

    @Test
    @DisplayName("no loss when parsing a valid content")
    public void shouldParseFullyValidContent() {
        var parser = new ExerciseParser();
        var result = parser.parse(validFullContent);
        assertNotNull(result);
        assertTrue(result.isPresent());
        var exercise = result.orElseThrow();
        assertEquals("*description*", exercise.getDescription());
        assertEquals(Difficulty.L1, exercise.getDifficulty());
        assertEquals(2, exercise.getConcepts().size());
        assertTrue(exercise.getConcepts().contains("concept1"));
        assertTrue(exercise.getConcepts().contains("concept2"));
        assertNotNull(exercise.getSignatureAndBody());
        assertNotNull(exercise.getUnitTests());
        assertNotNull(exercise.getSolution());
    }

    @Test
    @DisplayName("should return empty Optional when description is missing")
    public void shouldReturnEmptyWhenDescriptionMissing() {
        var missingDescriptionContent = """
                <DIFFICULTE>
                L2
                </DIFFICULTE>
                <CONCEPTS>
                loops
                </CONCEPTS>
                <SIGNATURE>
                public class Loop {}
                </SIGNATURE>
                <TESTS>
                public class LoopTest {}
                </TESTS>
                <SOLUTION>
                public class Loop {}
                </SOLUTION>
                """;
        var parser = new ExerciseParser();
        var result = parser.parse(missingDescriptionContent);
        assertTrue(result.isEmpty(), "Parser should return empty Optional when description is missing");
    }

    @Test
    @DisplayName("should return empty Optional when difficulty is missing")
    public void shouldReturnEmptyWhenDifficultyMissing() {
        var content = """
                <ENONCE>Test</ENONCE>
                <CONCEPTS>test</CONCEPTS>
                <SIGNATURE>class Test {}</SIGNATURE>
                <TESTS>class TestTest {}</TESTS>
                <SOLUTION>class Test {}</SOLUTION>
                """;
        var parser = new ExerciseParser();
        var result = parser.parse(content);
        assertTrue(result.isEmpty(), "Parser should return empty Optional when difficulty is missing");
    }

    @Test
    @DisplayName("Partial modification is merged with initial exercise")
    public void shouldMergePartiallyValidContent() {
        var difficultyModification = """
                <DIFFICULTE>
                L3
                </DIFFICULTE>
                """;
        var parser = new ExerciseParser();
        var exercise = new Exercise(
            "*description*",
            Difficulty.L1,
            List.of("concept 1", "concept 2"),
            "*signature*",
            "*tests*",
            "*solution*"
        );
        var result = parser.mergeExercise(exercise, difficultyModification);
        var exerciseAfterModif = result.orElseThrow();
        assertNotNull(exerciseAfterModif);
        assertEquals(Difficulty.L3, exerciseAfterModif.getDifficulty());
    }

}
