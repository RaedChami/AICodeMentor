package org.acme.student;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.ArrayList;

@Path("/api/student/submissions")
public class StudentSubmissionResource {
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getSubmissions()
    {
        return "";
    }

}