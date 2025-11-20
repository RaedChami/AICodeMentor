package org.acme.resource.student;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/api/student/submissions")
public class StudentSubmissionResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getSubmissions()
    {
        return "";
    }

}