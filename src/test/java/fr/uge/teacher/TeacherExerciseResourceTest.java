package fr.uge.teacher;

import fr.uge.exercise.Difficulty;
import fr.uge.exercise.Exercise;
import fr.uge.exercise.dto.UserPrompt;
import fr.uge.login.Login;
import fr.uge.login.Role;
import io.quarkus.test.common.http.TestHTTPEndpoint;
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
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
@TestHTTPEndpoint(TeacherExerciseResource.class)
public class TeacherExerciseResourceTest {
    @Inject
    EntityManager entityManager;

    private Login testCreator1;
    private Login testCreator2;
    private Exercise testExercise1;
    private Exercise testExercise2;

    private String validContent() {
        return "content for testing";
    }

    @BeforeEach
    @Transactional
    public void setup() {
        testCreator1 = new Login(
                "david",
                "starsky",
                Role.TEACHER
        );
        testCreator2 = new Login(
                "kenneth",
                "hutch",
                Role.TEACHER
        );
        entityManager.persist(testCreator1);
        entityManager.persist(testCreator2);

        testExercise1 = new Exercise(
                validContent(),
                Difficulty.L1,
                List.of("concept"),
                validContent(),
                validContent(),
                validContent()
        );
        testExercise2 = new Exercise(
                validContent(),
                Difficulty.L1,
                List.of("concept"),
                validContent(),
                validContent(),
                validContent()
        );

        testExercise1.setCreator(testCreator1);
        testExercise2.setCreator(testCreator1);
        entityManager.persist(testExercise1);
        entityManager.persist(testExercise2);
        entityManager.flush();
    }

    @AfterEach
    @Transactional
    public void cleanup() {
        entityManager.createQuery("DELETE FROM Exercise").executeUpdate();
        entityManager.createQuery("DELETE FROM Login").executeUpdate();
    }

    @Test
    @DisplayName("returns all exercises")
    public void testGetAll_Success() {
        given()
            .when()
            .get("/")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON);
    }

    @Test
    @DisplayName("returns exercises for valid user that has created exercises")
    public void testGetByUserId_Success() {
        given()
            .pathParam("userId", testCreator1.getId())
            .when()
            .get("/user/{userId}")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("$", hasSize(2))
            .body("[0].creator.id", is(testCreator1.getId().intValue()))
            .body("[1].creator.id", is(testCreator1.getId().intValue()));
    }

    @Test
    @DisplayName("returns nothing for valid user that has created no exercises")
    public void testGetByUserId_NoExercises() {
        given()
            .pathParam("userId", testCreator2.getId())
            .when()
            .get("/user/{userId}")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("$", hasSize(0));
    }

    @Test
    @DisplayName("HTTP error if negative user ID ")
    public void testGetByUserId_NegativeId() {
        given()
            .pathParam("userId", -1)
            .when()
            .get("/user/{userId}")
            .then()
            .statusCode(500);
    }

    @Test
    @DisplayName("HTTP error if zero ID throws error")
    public void testGetByUserId_ZeroId() {
        given()
            .pathParam("userId", 0)
            .when()
            .get("/user/{userId}")
            .then()
            .statusCode(500);
    }

    @Test
    @DisplayName("returns exercise by ID")
    public void testGet_Success() {
        given()
            .pathParam("id", testExercise1.getId())
            .when()
            .get("/{id}")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("id", is(testExercise1.getId().intValue()))
            .body("description", is("content for testing"))
            .body("solution", is("content for testing"))
            .body("unitTests", is("content for testing"))
            .body("creator.id", is(testCreator1.getId().intValue()))
            .body("creator.name", is("david"))
            .body("creator.lastName", is("starsky"));
    }

    @Test
    @DisplayName("GET of an exercise with a negative ID throws an error")
    public void testGet_NegativeId() {
        given()
            .pathParam("id", -1)
            .when()
            .get("/{id}")
            .then()
            .statusCode(500);
    }

    @Test
    @DisplayName("GET of an exercise with zero ID throws an error")
    public void testGet_ZeroId() {
        given()
            .pathParam("id", 0)
            .when()
            .get("/{id}")
            .then()
            .statusCode(500);
    }

    @Test
    @DisplayName("successfully deletes exercise if user is the creator")
    public void testDelete_Success() {
        long exerciseId = testExercise1.getId();
        long creatorId = testCreator1.getId();

        given()
            .pathParam("id", exerciseId)
            .queryParam("userId", creatorId)
            .when()
            .delete("/{id}")
            .then()
            .statusCode(204);

        given()
            .pathParam("id", exerciseId)
            .when()
            .get("/{id}")
            .then()
            .statusCode(404);
    }

    @Test
    @DisplayName("deletion fails when user is not the creator")
    public void testDelete_Unauthorized() {
        long exerciseId = testExercise1.getId();
        long otherUserId = testCreator2.getId();

        given()
            .pathParam("id", exerciseId)
            .queryParam("userId", otherUserId)
            .when()
            .delete("/{id}")
            .then()
            .statusCode(403);
    }

    @Test
    @DisplayName("deletion with negative exercise ID throws error")
    public void testDelete_NegativeExerciseId() {
        given()
                .pathParam("id", -1)
                .queryParam("userId", testCreator1.getId())
                .when()
                .delete("/{id}")
                .then()
                .statusCode(500);
    }

    @Test
    @DisplayName("deletion with negative user ID throws error")
    public void testDelete_NegativeUserId() {
        given()
            .pathParam("id", testExercise1.getId())
            .queryParam("userId", -1)
            .when()
            .delete("/{id}")
            .then()
            .statusCode(500);
    }

    @Test
    @DisplayName("deletion attempt of non-existent exercise returns HTTP error")
    public void testDelete_ExerciseNotFound() {
        given()
            .pathParam("id", 1991)
            .queryParam("userId", testCreator1.getId())
            .when()
            .delete("/{id}")
            .then()
            .statusCode(404);
    }

    @Test
    @DisplayName("modify with null prompt returns HTTP error")
    public void testModify_NullPrompt() {
        given()
            .pathParam("id", testExercise1.getId())
            .contentType(ContentType.JSON)
            .when()
            .put("/{id}/modify")
            .then()
            .statusCode(500);
    }

    @Test
    @DisplayName("modify with unauthorized user")
    public void testModify_Unauthorized() {
        var prompt = new UserPrompt(
                "Modify this exercise",
                testCreator2.getId()
        );

        given()
            .pathParam("id", testExercise1.getId())
            .contentType(ContentType.JSON)
            .body(prompt)
            .when()
            .put("/{id}/modify")
            .then()
            .statusCode(403);
    }

    @Test
    @DisplayName("modify with negative exercise ID")
    public void testModify_NegativeExerciseId() {
        var prompt = new UserPrompt(
                "Modify exercise",
                testCreator1.getId()
        );

        given()
            .pathParam("id", -1)
            .contentType(ContentType.JSON)
            .body(prompt)
            .when()
            .put("/{id}/modify")
            .then()
            .statusCode(500);
    }

    @Test
    @DisplayName("Complete workflow example of the resource")
    @Transactional
    public void testCompleteWorkflow() {
        given() // Gets an existing exercise
            .pathParam("id", testExercise1.getId())
            .when()
            .get("/{id}")
            .then()
            .statusCode(200)
            .body("description", is("content for testing"))
            .body("creator.lastName", is("starsky"));

        given() // deletes the existing exercise
            .pathParam("id", testExercise1.getId())
            .queryParam("userId", testCreator1.getId())
            .when()
            .delete("/{id}")
            .then()
            .statusCode(204);

        given() // check if exercise has been deleted
            .pathParam("id", testExercise1.getId())
            .when()
            .get("/{id}")
            .then()
            .statusCode(404);
    }
}