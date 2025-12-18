package fr.uge.exercise;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
public class ExerciseTest {

    private String validContent() {
        return "exercise content for testing";
    }

    @Test
    @DisplayName("exercise solution can't be null")
    public void shouldRejectNullSolution() {
        assertThrows(NullPointerException.class, () -> {
            new Exercise(
                    validContent(),
                    Difficulty.L1,
                    List.of("concept"),
                    validContent(),
                    validContent(),
                    null
            );
        });
    }

    @Test
    @DisplayName("exercise description can't be null")
    public void shouldRejectNullDescription() {
        assertThrows(NullPointerException.class, () -> {
            new Exercise(
                    null,
                    Difficulty.L1,
                    List.of("concept"),
                    validContent(),
                    validContent(),
                    validContent()
            );
        });
    }

    @Test
    @DisplayName("exercise difficulty can't be null")
    public void shouldRejectNullDifficulty() {
        assertThrows(NullPointerException.class, () -> {
            new Exercise(
                    validContent(),
                    null,
                    List.of("concept"),
                    validContent(),
                    validContent(),
                    validContent()
            );
        });
    }

    @Test
    @DisplayName("exercise concepts can't be null")
    public void shouldRejectNullConcepts() {
        assertThrows(NullPointerException.class, () -> {
            new Exercise(
                    validContent(),
                    Difficulty.L1,
                    null,
                    validContent(),
                    validContent(),
                    validContent()
            );
        });
    }

    @Test
    @DisplayName("exercise signature can't be null")
    public void shouldRejectNullSignature() {
        assertThrows(NullPointerException.class, () -> {
            new Exercise(
                    validContent(),
                    Difficulty.L1,
                    List.of("concept"),
                    null,
                    validContent(),
                    validContent()
            );
        });
    }

    @Test
    @DisplayName("exercise tests can't be null")
    public void shouldRejectNullJUnitTests() {
        assertThrows(NullPointerException.class, () -> {
            new Exercise(
                    validContent(),
                    Difficulty.L1,
                    List.of("concept"),
                    validContent(),
                    null,
                    validContent()
            );
        });
    }

    @Test
    @DisplayName("concepts are defensively copied")
    public void shouldCopyDefensivelyConceptsExercise() {
        var concepts = new ArrayList<>(List.of("concept1"));
        var exercise = new Exercise(
                validContent(),
                Difficulty.L1,
                concepts,
                validContent(),
                validContent(),
                validContent()
        );
        concepts.add("concept2");
        concepts.remove("concept1");
        assertEquals(List.of("concept1"), exercise.getConcepts());
    }

    @Test
    @DisplayName("exercise ID is not negative")
    public void exerciseIDShouldBePositive() {
        var concepts = new ArrayList<>(List.of("concept1"));
        var exercise = new Exercise(
                validContent(),
                Difficulty.L1,
                concepts,
                validContent(),
                validContent(),
                validContent()
        );
        assertThrows(IllegalArgumentException.class, () -> exercise.setId(-1L));
    }

}
