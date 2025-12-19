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

    @Inject
    EntityManager em;

    @Inject
    LlamaService llama;

    /**
     * Call the llm to get a hint on student code to help him by using the Junit test from the teacher
     * @param id
     * @param request
     * @return the answer of the llm
     */
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
