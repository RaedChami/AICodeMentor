package org.acme;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/hello")
public class GreetingResource {

    @Inject
    EntityManager entityManager;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from Quarkus REST";
    }

    @GET
    public List<MyEntity> get() {
        return entityManager.createNamedQuery("MyEntity.findAll", MyEntity.class)
                .getResultList();
    }
}
