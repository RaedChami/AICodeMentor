package fr.uge.exercise;

import fr.uge.login.Login;
import fr.uge.login.Role;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
public class ExerciseSubmittedTest {

    private Login validLogin() {
        return new Login("*name*", "*lastName*", Role.TEACHER);
    }

    private Exercise validExercise() {
        return new Exercise(
                "*description*",
                Difficulty.L1,
                List.of("concept1"),
                "*signature*",
                "*tests*",
                "*solution*"
        );
    }

    private String validSubmission() {
        return "public class Test {}";
    }

    @Test
    @DisplayName("submitted exercise login can't be null")
    public void shouldRejectNullLogin() {
        assertThrows(NullPointerException.class, () -> {
            new ExerciseSubmitted(
                    null,
                    validExercise(),
                    validSubmission()
            );
        });
    }

    @Test
    @DisplayName("submitted exercise can't be null")
    public void shouldRejectNullExercise() {
        assertThrows(NullPointerException.class, () -> {
            new ExerciseSubmitted(
                    validLogin(),
                    null,
                    validSubmission()
            );
        });
    }

    @Test
    @DisplayName("submitted submission can't be null")
    public void shouldRejectNullSubmission() {
        assertThrows(NullPointerException.class, () -> {
            new ExerciseSubmitted(
                    validLogin(),
                    validExercise(),
                    null
            );
        });
    }

    @Test
    @DisplayName("setSolutionSubmitted rejects null value")
    void shouldRejectNullSolutionInSetter() {
        var submitted = new ExerciseSubmitted(
                validLogin(),
                validExercise(),
                validSubmission()
        );
        assertThrows(NullPointerException.class, () ->
                submitted.setSolutionSubmitted(null)
        );
    }

    @Test
    @DisplayName("getter returns submitted solution")
    void shouldReturnSubmittedSolution() {
        var submitted = new ExerciseSubmitted(
                validLogin(),
                validExercise(),
                validSubmission()
        );
        assertEquals(validSubmission(), submitted.getSolutionSubmitted());
    }
}
