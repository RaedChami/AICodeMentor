package fr.uge.teacher;

import fr.uge.exercise.Difficulty;
import fr.uge.exercise.dto.ExerciseDTO;
import fr.uge.login.Login;
import fr.uge.login.LoginMapper;
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
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
@TestHTTPEndpoint(TeacherExerciseGenerateResource.class)
public class TeacherExerciseGenerateResourceTest {

    @Inject
    EntityManager entityManager;

    private Login testTeacher;
    private ExerciseDTO testExercise;
    @BeforeEach
    @Transactional
    public void setup() {
        testTeacher = new Login(
                "doc",
                "brown",
                Role.TEACHER
        );
        entityManager.persist(testTeacher);
        entityManager.flush();
        testExercise = new ExerciseDTO(
                null,
                "*description*",
                Difficulty.L1,
                List.of("concept"),
                "*signature*",
                "*tests*",
                "*solution*",
                LoginMapper.convertToDTO(testTeacher)
        );
    }

    @AfterEach
    @Transactional
    public void cleanup() {
        entityManager.createQuery("DELETE FROM Exercise").executeUpdate();
        entityManager.createQuery("DELETE FROM Login").executeUpdate();
    }

    @Test
    @DisplayName("save endpoint successfully saves valid exercise")
    public void testSave_Success() {
        given()
            .contentType(ContentType.JSON)
            .body(testExercise)
            .when()
            .post("/save")
            .then()
            .statusCode(200)
            .body("id", notNullValue())
            .body("description", is("*description*"))
            .body("solution", is("*solution*"))
            .body("unitTests", is("*tests*"))
            .body("creator.id", is(testTeacher.getId().intValue()))
            .body("creator.name", is(testTeacher.getName()))
            .body("creator.lastName", is(testTeacher.getLastName()));
    }

    @Test
    @DisplayName("save endpoint with null body returns error")
    public void testSave_NullBody() {
        given()
            .contentType(ContentType.JSON)
            .when()
            .post("/save")
            .then()
            .statusCode(anyOf(is(400), is(500)));
    }

    @Test
    @DisplayName("generate endpoint with null prompt returns error")
    public void testGenerate_NullPrompt() {
        given()
            .contentType(ContentType.JSON)
            .when()
            .post()
            .then()
            .statusCode(anyOf(is(400), is(500)));
    }

    @Test
    @DisplayName("generate endpoint with invalid JSON returns error")
    public void testGenerate_InvalidJSON() {
        var invalidJson = "{ invalid json }";
        given()
            .contentType(ContentType.JSON)
            .body(invalidJson)
            .when()
            .post()
            .then()
            .statusCode(anyOf(is(400), is(500)));
    }

}
