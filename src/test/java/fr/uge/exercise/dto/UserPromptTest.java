package fr.uge.exercise.dto;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
public class UserPromptTest {
    @Test
    public void userPromptIsNotNull() {
        assertThrows(NullPointerException.class, () -> new UserPrompt("*prompt*", null));
        assertThrows(NullPointerException.class, () -> new UserPrompt(null, null));
        assertThrows(NullPointerException.class, () -> new UserPrompt(null, 1L));
    }
}
