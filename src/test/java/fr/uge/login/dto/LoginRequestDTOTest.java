package fr.uge.login.dto;

import fr.uge.login.Role;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
public class LoginRequestDTOTest {
    @Test
    public void loginRequestDTORejectsNull() {
        assertThrows(NullPointerException.class, () -> new LoginRequestDTO("*name*", null));
        assertThrows(NullPointerException.class, () -> new LoginRequestDTO("*name*", null));
    }

}
