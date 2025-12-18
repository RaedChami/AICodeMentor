package fr.uge.exercise.dto;

import fr.uge.exercise.Difficulty;
import fr.uge.login.Role;
import fr.uge.login.dto.LoginDTO;
import io.quarkus.test.junit.QuarkusTest;
import org.apache.commons.lang3.builder.Diff;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
public class ExerciseDTOTest {
    @Test
    @DisplayName("an exercise can't be created with a negative ID")
    public void negativeId() {
        var creator = new LoginDTO(1L, "*name*", "*lastName*", Role.TEACHER);
        assertThrows(IllegalArgumentException.class, () ->
                new ExerciseDTO(
                        -1L,
                        "*dscription*",
                        Difficulty.L1,
                        List.of("concept1"),
                        "*signature*",
                        "*tests*",
                        "*solution*",
                        creator
                ));
    }

    @Test
    @DisplayName("null can't be set to fields of a DTO of an exercise")
    public void nullFieldInExerciseDTO() {
        var creator = new LoginDTO(1L, "*name*", "*lastName*", Role.TEACHER);
        assertThrows(NullPointerException.class, () ->
                new ExerciseDTO(
                        1L,
                        null,
                        Difficulty.L1,
                        List.of("concept1"),
                        null,
                        "*tests*",
                        null,
                        creator
                ));
    }

    @Test
    @DisplayName("concepts are defensively copied")
    public void shouldCopyDefensivelyExerciseDTO() {
        var creator = new LoginDTO(1L, "*name*", "*lastName*", Role.TEACHER);
        var concepts = new ArrayList<>(List.of("concept1"));
        var dto = new ExerciseDTO(
                        1L,
                        "*dscription*",
                        Difficulty.L1,
                        concepts,
                        "*signature*",
                        "*tests*",
                        "*solution*",
                        creator
                );
        concepts.add("concept2");
        concepts.remove("concept1");
        assertEquals(List.of("concept1"), dto.concepts());
    }

    @Test
    @DisplayName("concepts getter aare immutable")
    public void conceptsGettersAreImmutable() {
        var creator = new LoginDTO(1L, "*name*", "*lastName*", Role.TEACHER);
        var concepts = new ArrayList<>(List.of("concept1"));
        var dto = new ExerciseDTO(
                1L,
                "*dscription*",
                Difficulty.L1,
                concepts,
                "*signature*",
                "*tests*",
                "*solution*",
                creator
        );
        var gettered = dto.concepts();
        gettered.add("concept2");
        assertEquals(List.of("concept1"), dto.concepts());
    }
}
