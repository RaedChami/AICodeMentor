package fr.uge.login.dto;

import fr.uge.exercise.dto.UserPrompt;
import fr.uge.login.Role;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
public class LoginDTOTest {

    @Test
    public void loginDTORejectsNull() {
        assertThrows(IllegalArgumentException.class, () -> new LoginDTO(-1L, "*name*", "*lastName*", Role.TEACHER));
        assertThrows(NullPointerException.class, () -> new LoginDTO(1L, null, "*lastName*", Role.TEACHER));
        assertThrows(NullPointerException.class, () -> new LoginDTO(1L, "*name*", null, Role.TEACHER));
        assertThrows(NullPointerException.class, () -> new LoginDTO(1L, "*name*", "*lastName*", null));
    }
}
