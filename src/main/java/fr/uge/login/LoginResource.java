package fr.uge.login;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import fr.uge.login.dto.LoginDTO;
import fr.uge.login.dto.LoginRequestDTO;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Path("/api/login")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LoginResource {

    @Inject
    EntityManager entityManager;

    @POST
    @Transactional
    public LoginDTO create(LoginDTO login) {
        Objects.requireNonNull(login);
        validateLoginDTO(login);

        var existingLogin = findExistingLogin(login.name(), login.lastName());

        if (existingLogin.isPresent()) {
            return updateExistingLoginIfNeeded(existingLogin.get(), login.role());
        }

        return createNewLogin(login);
    }

    private void validateLoginDTO(LoginDTO login) {
        Objects.requireNonNull(login);
        if (login.name() == null || login.name().isBlank()) {
            throw new BadRequestException("Name is required");
        }
        if (login.lastName() == null || login.lastName().isBlank()) {
            throw new BadRequestException("Last name is required");
        }
        if (login.role() == null) {
            throw new BadRequestException("Role is required");
        }
    }

    private Optional<Login> findExistingLogin(String name, String lastName) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(lastName);
        var existing = entityManager.createQuery(
                        "SELECT l FROM Login l WHERE l.name = :name AND l.lastName = :lastName",
                        Login.class
                )
                .setParameter("name", name)
                .setParameter("lastName", lastName)
                .getResultList();

        return existing.isEmpty() ? Optional.empty() : Optional.of(existing.get(0));
    }

    private LoginDTO updateExistingLoginIfNeeded(Login existingLogin, Role newRole) {
        Objects.requireNonNull(existingLogin);
        Objects.requireNonNull(newRole);
        if (existingLogin.getRole() != newRole) {
            existingLogin.setRole(newRole);
            entityManager.merge(existingLogin);
        }
        return LoginMapper.convertToDTO(existingLogin);
    }

    private LoginDTO createNewLogin(LoginDTO login) {
        Objects.requireNonNull(login);
        var entity = LoginMapper.convertToEntity(login);
        entityManager.persist(entity);
        return LoginMapper.convertToDTO(entity);
    }

    @GET
    public List<Login> get(@QueryParam("name") String name, @QueryParam("lastName") String lastName) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(lastName);
        return entityManager.createQuery(
                        "SELECT l FROM Login l WHERE l.name = :name AND l.lastName = :lastName",
                        Login.class
                )
                .setParameter("name", name)
                .setParameter("lastName", lastName)
                .getResultList();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public void delete(@PathParam("id") long id) {
        if (id < 0) {
            throw new IllegalArgumentException("id < 0");
        }

        Login login = entityManager.find(Login.class, id);
        if (login != null) {
            entityManager.remove(login);
        } else {
            throw new NotFoundException("Login with id " + id + " not found");
        }
    }

    @POST
    @Path("/check")
    @Transactional
    public LoginDTO checkUser(LoginRequestDTO login) {
        Objects.requireNonNull(login);
        Optional<Login> existingLogin = findExistingLogin(login.name(), login.lastName());

        if (existingLogin.isEmpty()) {
            throw new NotFoundException("User not found");
        }

        return LoginMapper.convertToDTO(existingLogin.get());
    }
}