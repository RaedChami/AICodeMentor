package org.acme.student;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.ArrayList;

@Path("/api/student/exercises")
public class StudentExerciseResource {
    @GET
    @Produces(MediaType.TEXT_PLAIN)

    public String getExercises() {
        return "";
    }

}