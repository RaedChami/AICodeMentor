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

    private final EntityManager entityManager;
    @Inject
    LoginResource(EntityManager entityManager) {
        this.entityManager = Objects.requireNonNull(entityManager);
    }

    /**
     * Creates an account and checks if user exists
     * @param login DTO of user entity
     * @return DTO of user entity
     * @throws NoSuchFieldException Propagated exception from LoginMapper
     * @throws IllegalAccessException Propagated exception from LoginMapper
     */
    @POST
    @Transactional
    public LoginDTO create(LoginDTO login) throws NoSuchFieldException, IllegalAccessException {
        Objects.requireNonNull(login);
        validateLoginDTO(login);

        var existingLogin = findExistingLogin(login.name(), login.lastName());

        if (existingLogin.isPresent()) {
            return updateExistingLoginIfNeeded(existingLogin.get(), login.role());
        }

        return createNewLogin(login);
    }

    /**
     * Checks if name, last name and role are valid
     * @param login DTO of user
     */
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

    /**
     * Checks if a user exists
     * @param name first name of usr
     * @param lastName last name of user
     * @return an optional empty if the user is not found
     */
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

        return existing.isEmpty() ? Optional.empty() : Optional.of(existing.getFirst());
    }

    /**
     * Updates role of user
     * @param existingLogin existing account
     * @param newRole new role of the account
     * @return DTO of login entity
     */
    private LoginDTO updateExistingLoginIfNeeded(Login existingLogin, Role newRole) {
        Objects.requireNonNull(existingLogin);
        Objects.requireNonNull(newRole);
        if (existingLogin.getRole() != newRole) { // set the new role if the existing account's role is different
            existingLogin.setRole(newRole);
            entityManager.merge(existingLogin);
        }
        return LoginMapper.convertToDTO(existingLogin);
    }

    /**
     * Creates a new account for user
     * @param login DTO of user
     * @return DTO of user
     * @throws NoSuchFieldException Propagated exception from LoginMapper
     * @throws IllegalAccessException Propagated exception from LoginMapper
     */
    private LoginDTO createNewLogin(LoginDTO login) throws NoSuchFieldException, IllegalAccessException {
        Objects.requireNonNull(login);
        var entity = LoginMapper.convertToEntity(login);
        entityManager.persist(entity);
        return LoginMapper.convertToDTO(entity);
    }

    /**
     * Returns a user or many user corresponding to the same name and last name
     * @param name first name of user
     * @param lastName last name of usre
     * @return list of user
     */
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

    /**
     * Deletes the account of the user with the corresponding ID
     * @param id ID of the user
     */
    @DELETE
    @Path("/{id}")
    @Transactional
    public void delete(@PathParam("id") long id) {
        if (id < 0) {
            throw new IllegalArgumentException("id < 0");
        }
        var login = entityManager.find(Login.class, id);
        if (login != null) { // if user exists, deletes it
            entityManager.remove(login);
        } else {
            throw new NotFoundException("Login with id " + id + " not found");
        }
    }

    /**
     * Checks if a user that tries to log in exists
     * @param login user request DTO
     * @return Login DTO
     */
    @POST
    @Path("/check")
    @Transactional
    public LoginDTO checkUser(LoginRequestDTO login) {
        Objects.requireNonNull(login);
        var existingLogin = findExistingLogin(login.name(), login.lastName());
        if (existingLogin.isEmpty()) {
            throw new NotFoundException("User not found");
        }

        return LoginMapper.convertToDTO(existingLogin.get());
    }
}