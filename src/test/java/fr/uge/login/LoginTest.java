package fr.uge.login;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class LoginTest {

    private String validName() {
        return "gaston";
    }

    private String validLastName() {
        return "lagaffe";
    }

    @Test
    @DisplayName("Login name can't be null")
    public void shouldRejectNullName() {
        assertThrows(NullPointerException.class, () -> new Login(null, validLastName(), Role.TEACHER));
    }

    @Test
    @DisplayName("Login last name can't be null")
    public void shouldRejectNullLastName() {
        assertThrows(NullPointerException.class, () -> new Login(validName(), null, Role.TEACHER));
    }

    @Test
    @DisplayName("Login role can't be null")
    public void shouldRejectNullRol() {
        assertThrows(NullPointerException.class, () -> new Login(validName(), validLastName(), null));
    }

    @Test
    @DisplayName("Login is created when all fields are valid")
    public void shouldBeValid() {
        var login = new Login(validName(), validLastName(), Role.TEACHER);
        assertNotNull(login);
        assertEquals(validName(), login.getName());
        assertEquals(validLastName(), login.getLastName());
        assertEquals(Role.TEACHER, login.getRole());
    }

    @Test
    @DisplayName("setRole rejects null value")
    void setRoleShouldRejectNull() {
        var login = new Login(validName(), validLastName(), Role.TEACHER);
        assertThrows(NullPointerException.class, () ->
                login.setRole(null)
        );
    }
}