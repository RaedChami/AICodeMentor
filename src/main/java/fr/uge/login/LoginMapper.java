package fr.uge.login;

import fr.uge.login.dto.LoginDTO;

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

    public static Login convertToEntity(LoginDTO loginDTO) {
        Objects.requireNonNull(loginDTO);
        var login =  new Login(
                loginDTO.name(),
                loginDTO.lastName(),
                loginDTO.role()
        );
        if (loginDTO.id() != null) {
            try {
                var idField = Login.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(login, loginDTO.id());
            } catch (Exception e) {
                throw new RuntimeException("Cannot set login ID", e);
            }
        }

        return login;
    }

}
