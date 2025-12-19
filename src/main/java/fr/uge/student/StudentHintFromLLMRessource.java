package fr.uge.student;

import de.kherud.llama.InferenceParameters;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import fr.uge.exercise.Exercise;
import fr.uge.llm.LlamaService;

@Path("/api/student/exercises")
public class StudentHintFromLLMRessource {

    private final EntityManager em;
    private final LlamaService llama;
    @Inject
    StudentHintFromLLMRessource(EntityManager entityManager, LlamaService llama) {
        this.em = entityManager;
        this.llama = llama;
    }

    @POST
    @Path("/get-hint/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response getHintFromLlama(@PathParam("id") long id, StudentSubmissionResource.CompileRequest request) {

        Exercise exercise = em.find(Exercise.class, id);
        if (exercise == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Exercise not found").build();
        }

        String answer = llama.getHint(request.getCode(), exercise.getUnitTests());

        return Response.ok(answer).build();
    }
}
