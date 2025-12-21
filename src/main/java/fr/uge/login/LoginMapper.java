package fr.uge.login;

import fr.uge.login.dto.LoginDTO;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

/**
 * Mapping class for a user.
 * Allows to convert a user entity into a DTO and vice versa
 */
public class LoginMapper {
    public static LoginDTO convertToDTO(Login login) {
        Objects.requireNonNull(login);
        return new LoginDTO(
                login.getId(),
                login.getName(),
                login.getLastName(),
                login.getRole()
        );
    }

    public static Login convertToEntity(LoginDTO loginDTO) throws NoSuchFieldException, IllegalAccessException {
        Objects.requireNonNull(loginDTO);
        var login =  new Login(
                loginDTO.name(),
                loginDTO.lastName(),
                loginDTO.role()
        );
        if (loginDTO.id() != null) { // reflexion used to restore the ID of the login entity
            var idField = Login.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(login, loginDTO.id());
        }

        return login;
    }

}
