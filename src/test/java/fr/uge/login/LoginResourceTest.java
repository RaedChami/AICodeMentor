package fr.uge.login;

import fr.uge.login.dto.LoginDTO;
import fr.uge.login.dto.LoginRequestDTO;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.urlEncodingEnabled;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

@QuarkusTest
@TestHTTPEndpoint(LoginResource.class)
public class LoginResourceTest {

    private final EntityManager entityManager;
    @Inject
    LoginResourceTest(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    private String validNameForTests() {
        return "gaston";
    }

    private String validLastNameForTests() {
        return "lagaffe";
    }

    private Role validRoleForTests() {
        return Role.STUDENT;
    }

    @AfterEach
    @Transactional
    public void cleanup() {
        entityManager.createQuery("DELETE FROM Login").executeUpdate();
    }

    @Test
    @DisplayName("creating a login is successful")
    public void successfulCreateLogin() {
        var loginDTO = new LoginDTO(null, validNameForTests(), validLastNameForTests(), validRoleForTests());
        given()
            .contentType(ContentType.JSON)
            .body(loginDTO)
            .when()
            .post()
            .then()
            .statusCode(200)
            .body("id", notNullValue())
            .body("name", is(validNameForTests()))
            .body("lastName", is(validLastNameForTests()))
            .body("role", is(validRoleForTests().name()));
    }

    @Test
    @DisplayName("getting an existing login is successful")
    public void testGetLogin_Success() {
        var loginDTO = new LoginDTO(null, validNameForTests(), validLastNameForTests(), validRoleForTests());
        given()
            .contentType(ContentType.JSON)
            .body(loginDTO)
            .when()
            .post();

        given()
            .queryParam("name", validNameForTests())
            .queryParam("lastName", validLastNameForTests())
            .when()
            .get()
            .then()
            .statusCode(200)
            .body("[0].name", is("gaston"))
            .body("[0].lastName", is("lagaffe"))
            .body("[0].role", is("STUDENT"));
    }

    @Test
    @DisplayName("can't delete a login that is negative")
    public void testDeleteLogin_NegativeId() {
        given()
                .pathParam("id", -1L)
                .when()
                .delete("/{id}")
                .then()
                .statusCode(500);
    }

    @Test
    @DisplayName("deleting an existing login is successful")
    public void testDeleteLogin_Success() {
        var loginDTO = new LoginDTO(null, validNameForTests(), validLastNameForTests(), validRoleForTests());
        var id = given()
                    .contentType(ContentType.JSON)
                    .body(loginDTO)
                    .when()
                    .post()
                    .then()
                    .extract()
                    .jsonPath()
                    .getLong("id");

        given()
            .pathParam("id", id)
            .when()
            .delete("/{id}")
            .then()
            .statusCode(204);

        given()
            .queryParam("name", "gaston")
            .queryParam("lastName", "lagaffe")
            .when()
            .get()
            .then()
            .statusCode(200);
    }

    @Test
    @DisplayName("Verification of existing user is valid")
    public void testCheckUser_Success() {
        var loginDTO = new LoginDTO(null, validNameForTests(), validLastNameForTests(), validRoleForTests());
        given()
            .contentType(ContentType.JSON)
            .body(loginDTO)
            .when()
            .post();

        var requestDTO = new LoginRequestDTO(validNameForTests(), validLastNameForTests());
        given()
            .contentType(ContentType.JSON)
            .body(requestDTO)
            .when()
            .post("/check")
            .then()
            .statusCode(200)
            .body("name", is("gaston"))
            .body("lastName", is("lagaffe"))
            .body("role", is("STUDENT"));
    }

}
