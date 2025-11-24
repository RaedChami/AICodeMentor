package org.acme.service;

public class ExerciseGenerationException extends RuntimeException {
    public ExerciseGenerationException(String message, Throwable cause) {
        super();
    }
    public ExerciseGenerationException(String message) {
        super(message);
    }
    public ExerciseGenerationException(Throwable cause) {
        super(cause);
    }
}
