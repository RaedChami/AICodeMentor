package org.acme.service;

import jakarta.ws.rs.WebApplicationException;

public class ExerciseGenerationException extends WebApplicationException {
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
