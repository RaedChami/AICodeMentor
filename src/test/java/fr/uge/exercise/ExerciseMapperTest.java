package fr.uge.exercise;

import fr.uge.exercise.dto.ExerciseDTO;
import fr.uge.login.Login;
import fr.uge.login.Role;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class ExerciseMapperTest {

    @Test
    @DisplayName("mapper doesn't convert to DTO a null")
    public void convertToDTONullTest() {
        assertThrows(NullPointerException.class, () -> ExerciseMapper.convertToDTO(null));
    }

    @Test
    @DisplayName("exercise entity is correctly converted to DTO")
    public void convertToDTOTest() {
        var exercise = new Exercise(
                "*description*",
                Difficulty.L1,
                List.of("concept1"),
                "*signature*",
                "*tests*",
                "*solution*"
        );
        var creator = new Login("*name*", "*lastName*", Role.TEACHER);
        exercise.setId(1L);
        exercise.setCreator(creator);
        var dto = ExerciseMapper.convertToDTO(exercise);
        assertNotNull(dto);
        assertEquals(1L, dto.id());
        assertEquals("*description*", dto.description());
        assertEquals(Difficulty.L1, dto.difficulty());
        assertEquals(List.of("concept1"), dto.concepts());
        assertEquals("*signature*", dto.signatureAndBody());
        assertEquals("*tests*", dto.unitTests());
        assertEquals("*solution*", dto.solution());
        assertNotNull(dto.creator());
    }

    @Test
    @DisplayName("mapper doesn't convert to entity a null")
    public void convertToEntityNullTest() {
        assertThrows(NullPointerException.class, () -> ExerciseMapper.convertToEntity(null));
    }

    @Test
    @DisplayName("convertToEntity sets id using reflection when present")
    void shouldSetIdWhenPresentInDTO() throws Exception {
        var dto = new ExerciseDTO(
                10L,
                "description",
                Difficulty.L3,
                List.of("streams"),
                "signature",
                "tests",
                "solution",
                null
        );

        var exercise = ExerciseMapper.convertToEntity(dto);
        assertEquals(10L, exercise.getId());
    }
}
