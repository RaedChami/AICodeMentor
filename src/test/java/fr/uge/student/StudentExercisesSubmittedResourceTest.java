package fr.uge.student;

import fr.uge.exercise.Difficulty;
import fr.uge.exercise.Exercise;
import fr.uge.exercise.ExerciseSubmitted;
import fr.uge.exercise.dto.ExerciseSubmittedDTO;
import fr.uge.login.Login;
import fr.uge.login.Role;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class StudentExercisesSubmittedResourceTest {

    @Inject
    EntityManager em;

    private Login student;
    private Exercise exercise;

    @BeforeEach
    @Transactional
    public void setup() {
        student = new Login("student", "pwd", Role.STUDENT);
        em.persist(student);

        exercise = new Exercise();
        exercise.setDescription("Exercise description");
        exercise.setCreator(student);
        exercise.setDifficulty(Difficulty.L1);
        exercise.setConcepts(List.of("loops", "conditions"));
        exercise.setSignatureAndBody("int f(int x) { return x; }");
        exercise.setTests("assert f(1)==1;");
        exercise.setSolution("return x;");
        em.persist(exercise);

        em.flush();
    }




    @AfterEach
    @Transactional
    public void cleanup() {
        em.createQuery("DELETE FROM ExerciseSubmitted").executeUpdate();
        em.createQuery("DELETE FROM Exercise").executeUpdate();
        em.createQuery("DELETE FROM Login").executeUpdate();
    }

    @Test
    @DisplayName("should return empty list if no exercise was submitted")
    public void testFindExercisesByLoginId_Empty() {
        given()
                .queryParam("loginId", student.getId())
                .when()
                .get("/api/student/exercises-submitted")
                .then()
                .statusCode(200)
                .body("$", hasSize(0));
    }

    @Test
    @DisplayName("should return exercises submitted by the student")
    public void testFindExercisesByLoginId_Success() {
        ExerciseSubmittedDTO dto = new ExerciseSubmittedDTO(
                1,
                student.getId(),
                exercise.getId(),
                "solution"
        );

        given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when()
                .post("/api/student/exercises-submitted")
                .then()
                .statusCode(204);

        given()
                .queryParam("loginId", student.getId())
                .when()
                .get("/api/student/exercises-submitted")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].id", equalTo(exercise.getId().intValue()))
                .body("[0].description", equalTo("Exercise description"));
    }


    @Test
    @DisplayName("should submit an exercise successfully")
    public void testSubmitExercise_Success() {
        ExerciseSubmittedDTO dto = new ExerciseSubmittedDTO(
                1,
                student.getId(),
                exercise.getId(),
                "my solution"
        );

        given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when()
                .post("/api/student/exercises-submitted")
                .then()
                .statusCode(204);

        var result = em.createQuery(
                "SELECT es FROM ExerciseSubmitted es",
                ExerciseSubmitted.class
        ).getResultList();

        assertEquals(1, result.size());
        assertEquals("my solution", result.getFirst().getSolutionSubmitted());
        assertEquals(student.getId(), result.getFirst().getLogin().getId());
        assertEquals(exercise.getId(), result.getFirst().getExercise().getId());
    }

    @Test
    @DisplayName("should return 404 if login does not exist")
    public void testSubmitExercise_LoginNotFound() {
        ExerciseSubmittedDTO dto = new ExerciseSubmittedDTO(
                1,
                999L,
                exercise.getId(),
                "solution"
        );

        given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when()
                .post("/api/student/exercises-submitted")
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("should return 404 if exercise does not exist")
    public void testSubmitExercise_ExerciseNotFound() {
        ExerciseSubmittedDTO dto = new ExerciseSubmittedDTO(
                1,
                student.getId(),
                999L,
                "solution"
        );

        given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when()
                .post("/api/student/exercises-submitted")
                .then()
                .statusCode(404);
    }


    @Test
    @DisplayName("should return empty string if submission does not exist")
    public void testGetSubmittedSolution_Empty() {
        given()
                .queryParam("loginId", student.getId())
                .queryParam("exerciseId", exercise.getId())
                .when()
                .get("/api/student/exercises-submitted/solution")
                .then()
                .statusCode(200)
                .body(equalTo(""));
    }

    @Test
    @DisplayName("should return submitted solution")
    public void testGetSubmittedSolution_Success() {
        ExerciseSubmittedDTO dto = new ExerciseSubmittedDTO(
                1,
                student.getId(),
                exercise.getId(),
                "expected solution"
        );

        given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when()
                .post("/api/student/exercises-submitted")
                .then()
                .statusCode(204);

        given()
                .queryParam("loginId", student.getId())
                .queryParam("exerciseId", exercise.getId())
                .when()
                .get("/api/student/exercises-submitted/solution")
                .then()
                .statusCode(200)
                .body(equalTo("expected solution"));
    }

}
