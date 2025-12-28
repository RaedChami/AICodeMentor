package fr.uge.exercise.dto;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
public class UserPromptTest {
    @Test
    public void userPromptIsNotNull() {
        assertThrows(IllegalArgumentException.class, () -> new UserPrompt("*prompt*", -1));
        assertThrows(IllegalArgumentException.class, () -> new UserPrompt(null, -1));
        assertThrows(NullPointerException.class, () -> new UserPrompt(null, 1L));
    }
}
