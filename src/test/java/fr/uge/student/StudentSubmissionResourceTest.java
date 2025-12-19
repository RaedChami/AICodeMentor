package fr.uge.student;

import fr.uge.exercise.Exercise;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class StudentSubmissionResourceTest {

    @Inject
    EntityManager em;

    private Exercise exercise;

    @BeforeEach
    @Transactional
    public void setup() {
        exercise = new Exercise();
        exercise.setDescription("Exercise for execution");
        exercise.setTests("""
                import org.junit.jupiter.api.Test;
                import static org.junit.jupiter.api.Assertions.*;

                public class SampleTest {
                    @Test
                    void testAdd() {
                        assertEquals(2, Sample.add(1,1));
                    }
                }
                """);
        em.persist(exercise);
        em.flush();
    }

    @AfterEach
    @Transactional
    public void cleanup() {
        em.createQuery("DELETE FROM Exercise").executeUpdate();
    }

    @Test
    @DisplayName("should return 404 if exercise does not exist")
    public void testRunTests_ExerciseNotFound() {
        var request = new StudentSubmissionResource.CompileRequest();
        request.setCode("class Sample {}");

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/student/exercises/run-tests/9999")
                .then()
                .statusCode(404)
                .body(equalTo("Exercise not found"));
    }

    @Test
    @DisplayName("should return test output for valid submission")
    public void testRunTests_Success() {
        var request = new StudentSubmissionResource.CompileRequest();
        request.setCode("""
                public class Sample {
                    public static int add(int a, int b) {
                        return a + b;
                    }
                }
                """);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/student/exercises/run-tests/" + exercise.getId())
                .then()
                .statusCode(200)
                .body(
                        anyOf(
                                containsString("Tests exécutés"),
                                containsString("Succès"),
                                containsString("Échecs")
                        )
                );
    }

    @Test
    @DisplayName("should return compilation or assertion failure output")
    public void testRunTests_Failure() {
        var request = new StudentSubmissionResource.CompileRequest();
        request.setCode("""
                public class Sample {
                    public static int add(int a, int b) {
                        return a - b;
                    }
                }
                """);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/student/exercises/run-tests/" + exercise.getId())
                .then()
                .statusCode(200);
    }
}
