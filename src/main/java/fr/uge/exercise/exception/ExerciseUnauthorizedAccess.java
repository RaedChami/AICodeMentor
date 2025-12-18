package fr.uge.exercise.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class ExerciseUnauthorizedAccess extends WebApplicationException {
    public ExerciseUnauthorizedAccess(String message, Throwable cause) {
        super(message, cause);
    }
    public ExerciseUnauthorizedAccess(String message) {
        super(message, Response.Status.FORBIDDEN);
    }
    public ExerciseUnauthorizedAccess(Throwable cause) { super(cause); }
}
