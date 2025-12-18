package fr.uge.exercise.exception;

public class ExerciseGenerationException extends RuntimeException {
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
