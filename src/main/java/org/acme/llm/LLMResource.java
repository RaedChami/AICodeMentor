package org.acme.llm;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/llm")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LLMResource {

    @Inject
    JlamaService jlamaService;

    public record PromptRequest(String prompt) {}
    public record PromptResponse(String response) {}

    @POST
    @Path("/prompt")
    public Response handlePrompt(PromptRequest req) {
        if (req == null || req.prompt() == null || req.prompt().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Le prompt ne peut pas être vide.")
                    .build();
        }

        String result = jlamaService.ask(req.prompt());
        return Response.ok(new PromptResponse(result)).build();
    }
}
