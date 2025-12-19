package fr.uge.student.service;

import fr.uge.exercise.Exercise;
import fr.uge.exercise.service.ExerciseCompiler;
import fr.uge.exercise.service.ExerciseParser;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class StudentSubmissionServiceTest {

    @Inject
    StudentSubmissionService studentSubmissionService;

    @Inject
    EntityManager entityManager;

    @Inject
    ExerciseCompiler exerciseCompiler;

    @Inject
    ExerciseParser exerciseParser;

    private Exercise testExercise;

    @BeforeEach
    @Transactional
    public void setup() {
        testExercise = new Exercise();
        testExercise.setDescription("Test exercise");
        testExercise.setTests("""
                import org.junit.jupiter.api.Test;
                import static org.junit.jupiter.api.Assertions.*;

                public class SampleTest {
                    @Test
                    void testOk() {
                        assertEquals(2, Sample.add(1,1));
                    }
                }
                """);
        entityManager.persist(testExercise);
        entityManager.flush();
    }

    @AfterEach
    @Transactional
    public void cleanup() {
        entityManager.createQuery("DELETE FROM Exercise").executeUpdate();
    }

    @Test
    @DisplayName("should throw NotFoundException if exercise does not exist")
    public void testGetTestOutput_ExerciseNotFound() {
        assertThrows(NotFoundException.class, () -> {
            studentSubmissionService.getTestOutput(9999L, "class Sample {}");
        });
    }

    @Test
    @DisplayName("should return test output for valid exercise and valid student code")
    public void testGetTestOutput_Success() throws IOException, InterruptedException {
        String studentCode = """
                public class Sample {
                    public static int add(int a, int b) {
                        return a + b;
                    }
                }
                """;

        String output = studentSubmissionService.getTestOutput(testExercise.getId(), studentCode);

        assertNotNull(output);
        assertFalse(output.isBlank(), "Expected non-empty test output");
    }

    @Test
    @DisplayName("should return compilation or test failure output for invalid student code")
    public void testGetTestOutput_FailingStudentCode() throws IOException, InterruptedException {
        String studentCode = """
                public class Sample {
                    public static int add(int a, int b) {
                        return a - b;
                    }
                }
                """;

        String output = studentSubmissionService.getTestOutput(testExercise.getId(), studentCode);

        assertNotNull(output);
        assertFalse(output.isBlank());
    }

    @Test
    @DisplayName("should return timeout message when execution exceeds time limit")
    public void testGetTestOutput_Timeout() throws IOException, InterruptedException {
        String studentCode = """
                public class Sample {
                    public static int add(int a, int b) {
                        while(true) {}
                    }
                }
                """;

        String output = studentSubmissionService.getTestOutput(testExercise.getId(), studentCode);

        assertEquals(
                "Execution du test interrompue : dépassement du temps limite",
                output
        );
    }

    @Test
    @DisplayName("null student code throws NullPointerException")
    public void testGetTestOutput_NullStudentCode() {
        assertThrows(NullPointerException.class, () -> {
            studentSubmissionService.getTestOutput(testExercise.getId(), null);
        });
    }
}
