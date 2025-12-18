package fr.uge.llm;

import fr.uge.exercise.Exercise;
import fr.uge.exercise.service.ExerciseParser;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
public class LlamaServiceTest {
    @Inject
    LlamaService llamaService;
    @Inject
    ExerciseParser exerciseParser;

    private String validContent() {
        return "content for testing";
    }

    @Test
    @DisplayName("should not get hint if student code is null")
    public void testGetHintWithNull() {
        assertThrows(NullPointerException.class, () -> {
            llamaService.getHint(null, validContent());
        });
    }

    @Test
    @DisplayName("should not get hint if exerciseTest is null")
    public void testGetHintWithNull2() {
        assertThrows(NullPointerException.class, () -> {
            llamaService.getHint(validContent(), null);
        });
    }

    @Test
    @DisplayName("should not generate exercise if null description")
    public void testGenerateExercise_NullDescription() {
        assertThrows(NullPointerException.class, () -> {
            llamaService.generateExercise(null);
        });
    }

    @Test
    @DisplayName("should not generate modification if initial exercise is null")
    public void testModifyExercise_NullExercise() {
        assertThrows(NullPointerException.class, () -> {
            llamaService.modifyExercise(null, "add a unit test");
        });
    }

    @Test
    @DisplayName("should not generate modification if modification request is null")
    public void testModifyExercise_NullDescription() {
        assertThrows(NullPointerException.class, () -> {
            llamaService.modifyExercise(new Exercise(), null);
        });
    }

}
