package org.acme.resource;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.acme.dto.LoginDTO;
import org.acme.dto.LoginRequestDTO;
import org.acme.mapper.LoginMapper;
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
    public LoginDTO create(LoginDTO login) {

        Objects.requireNonNull(login);
        if (login.name() == null || login.name().isBlank())
            throw new BadRequestException("Name is required");

        if (login.lastName() == null || login.lastName().isBlank())
            throw new BadRequestException("Last name is required");

        if (login.role() == null)
            throw new BadRequestException("Role is required");

        List<Login> existing = em.createQuery(
                        "SELECT l FROM Login l WHERE l.name = :name AND l.lastName = :lastName",
                        Login.class
                )
                .setParameter("name", login.name())
                .setParameter("lastName", login.lastName())
                .getResultList();

        if (!existing.isEmpty()) {
            var found = existing.get(0);
            if (found.getRole() != login.role()) {
                found.setRole(login.role());
                em.merge(found);
            }
            return LoginMapper.convertToDTO(found);
        }
        var entity = LoginMapper.convertToEntity(login);
        em.persist(entity);
        return LoginMapper.convertToDTO(entity);
    }

    @GET
    public List<Login> get(Login login) {
        Objects.requireNonNull(login);
        return em.createQuery(
                        "SELECT l FROM Login l WHERE l.name = :name AND l.lastName = :lastName",
                        Login.class
                )
                .setParameter("name", login.getName())
                .setParameter("lastName", login.getLastName())
                .getResultList();
    }


    @DELETE
    @Path("/{id}")
    @Transactional
    public void delete(@PathParam("id") Long id) {
        if (id != null && id < 0)
            throw new IllegalArgumentException("id < 0");

        Login login = em.find(Login.class, id);
        if (login != null) {
            em.remove(login);
        } else {
            throw new NotFoundException("Login with id " + id + " not found");
        }
    }

    @POST
    @Path("/check")
    @Transactional
    public LoginDTO checkUser(LoginRequestDTO login) {
        Objects.requireNonNull(login);
        List<Login> existing = em.createQuery(
                        "SELECT l FROM Login l WHERE l.name = :name AND l.lastName = :lastName",
                        Login.class
                )
                .setParameter("name", login.name())
                .setParameter("lastName", login.lastName())
                .getResultList();

        if (existing.isEmpty())
            throw new NotFoundException("User not found");

        return LoginMapper.convertToDTO(existing.get(0));
    }

    @GET
    @Path("/debug")
    public List<LoginDTO> debug() {
        return em.createQuery("SELECT l FROM Login l", Login.class)
                .getResultList()
                .stream()
                .map(LoginMapper::convertToDTO)
                .toList();
    }

}
