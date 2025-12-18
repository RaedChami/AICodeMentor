package fr.uge.login;

import fr.uge.login.dto.LoginDTO;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
public class LoginMapperTest {

    @Test
    @DisplayName("mapper doesn't convert to DTO a null")
    public void convertToDTONullTest() {
        assertThrows(NullPointerException.class, () -> LoginMapper.convertToDTO(null));
    }

    @Test
    @DisplayName("login entity is correctly converted to DTO")
    public void convertToDTOTest() {
        var login = new Login("*name*", "*lastName*", Role.TEACHER);
        var dto = LoginMapper.convertToDTO(login);
        assertNotNull(dto);
        assertEquals("*name*", dto.name());
        assertEquals("*lastName*", dto.lastName());
        assertEquals(Role.TEACHER, dto.role());
    }

    @Test
    @DisplayName("mapper doesn't convert to entity a null")
    public void convertToEntityNullTest() {
        assertThrows(NullPointerException.class, () -> LoginMapper.convertToEntity(null));
    }

    @Test
    @DisplayName("convertToEntity sets id using reflection when present")
    void shouldSetIdWhenPresentInDTO() throws Exception {
        var dto = new LoginDTO(
                10L,
                "*name*",
                "*lastName*",
                Role.TEACHER
        );

        var exercise = LoginMapper.convertToEntity(dto);
        assertEquals(10L, exercise.getId());
    }

}