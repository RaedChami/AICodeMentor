package org.acme.mapper;

import org.acme.dto.ExerciseDTO;
import org.acme.dto.LoginDTO;
import org.acme.model.Exercise;
import org.acme.model.Login;

import java.util.Objects;

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
