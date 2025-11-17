package org.acme.resource;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.acme.model.Login;

import java.util.List;
import java.util.Objects;

@Path("/api/login")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LoginResource {

    @Inject
    EntityManager em;

    @POST
    @Transactional
    public Login create(Login login) {

        Objects.requireNonNull(login);

        if (login.name == null || login.name.isBlank())
            throw new BadRequestException("Name is required");

        if (login.lastName == null || login.lastName.isBlank())
            throw new BadRequestException("Last name is required");

        if (login.role == null)
            throw new BadRequestException("Role is required");

        // Chercher utilisateur existant (même nom + prénom)
        List<Login> existing = em.createQuery(
                        "SELECT l FROM Login l WHERE l.name = :name AND l.lastName = :lastName",
                        Login.class
                )
                .setParameter("name", login.name)
                .setParameter("lastName", login.lastName)
                .getResultList();

        if (!existing.isEmpty()) {
            Login found = existing.get(0);

            // Si rôle différent, on met à jour le rôle
            if (found.role != login.role) {
                found.role = login.role;
                em.merge(found);
            }

            return found;
        }

        // Nouvel utilisateur → rôle obligatoire
        em.persist(login);
        return login;
    }

    @GET
    public List<Login> get(Login login) {
        return em.createQuery(
                        "SELECT l FROM Login l WHERE l.name = :name AND l.lastName = :lastName",
                        Login.class
                )
                .setParameter("name", login.name)
                .setParameter("lastName", login.lastName)
                .getResultList();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public void delete(@PathParam("id") Long id) {
        Login login = em.find(Login.class, id);
        if (login != null) {
            em.remove(login);
        } else {
            throw new NotFoundException("Login with id " + id + " not found");
        }
    }

    @POST
    @Path("/check")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Login checkUser(Login login) {

        List<Login> existing = em.createQuery(
                        "SELECT l FROM Login l WHERE l.name = :name AND l.lastName = :lastName",
                        Login.class
                )
                .setParameter("name", login.name)
                .setParameter("lastName", login.lastName)
                .getResultList();

        if (existing.isEmpty()) {
            throw new NotFoundException("User not found");
        }

        return existing.get(0);
    }
}
