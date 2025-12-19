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
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
public class StudentHintFromLLMResourceTest {

    @Inject
    EntityManager em;

    private Exercise exercise;

    @BeforeEach
    @Transactional
    public void setup() {
        exercise = new Exercise();
        exercise.setDescription("Exercise for hint");
        exercise.setTests("""
                import org.junit.jupiter.api.Test;

                public class SampleTest {
                    @Test
                    void dummy() {}
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
    public void testGetHint_ExerciseNotFound() {
        var request = new StudentSubmissionResource.CompileRequest();
        request.setCode("class Sample {}");

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/student/exercises/get-hint/9999")
                .then()
                .statusCode(404)
                .body(equalTo("Exercise not found"));
    }
}
