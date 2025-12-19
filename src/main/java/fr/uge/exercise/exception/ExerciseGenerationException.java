package fr.uge.exercise.exception;

import java.io.Serial;

public class ExerciseGenerationException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;
    public ExerciseGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
    public ExerciseGenerationException(String message) {
        super(message);
    }
    public ExerciseGenerationException(Throwable cause) {
        super(cause);
    }
}
