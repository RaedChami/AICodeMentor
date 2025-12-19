package fr.uge.exercise.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.io.Serial;

public class ExerciseUnauthorizedAccess extends WebApplicationException {
    @Serial
    private static final long serialVersionUID = 1L;
    public ExerciseUnauthorizedAccess(String message, Throwable cause) {
        super(message, cause);
    }
    public ExerciseUnauthorizedAccess(String message) {
        super(message, Response.Status.FORBIDDEN);
    }
    public ExerciseUnauthorizedAccess(Throwable cause) { super(cause); }
}
