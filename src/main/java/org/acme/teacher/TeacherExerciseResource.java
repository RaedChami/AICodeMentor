package org.acme.teacher;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

import org.acme.model.Exercise;
import org.acme.llm.JlamaService;

@Path("/api/teacher/exercises")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TeacherExerciseResource {

    @Inject
    JlamaService jlamaService;

    private final List<Exercise> exercises = new ArrayList<>();

    @GET
    public Response listExercises() {
        return Response.ok(exercises).build();
    }

    public record PromptRequest(String prompt) {
    }

    @POST
    @Path("/generate")
    public Response generateExercise(PromptRequest req) throws IOException {
        Objects.requireNonNull(req);
        Exercise ex = jlamaService.generateExercise(req.prompt());
        exercises.add(ex);
        return Response.ok(ex).build();
    }
}
