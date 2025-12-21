package fr.uge.login;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@NamedQuery(name = "Login.findAll", query = "SELECT l FROM Login l")
public class Login {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String lastName;
    private Role role;

    /**
     * Default constructor of login entity
     */
    public Login() {}

    public Login(String name, String lastName, Role role) {
        this.name = Objects.requireNonNull(name);
        this.lastName = Objects.requireNonNull(lastName);
        this.role = Objects.requireNonNull(role);
    }

    /**
     * ID of author getter
     * @return ID of author
     */
    public Long getId() {
        return id;
    }

    /**
     * first name of author getter
     * @return first name of author
     */
    public String getName() {
        return name;
    }

    /**
     * last name of author getter
     * @return last name of author
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * role of author getter
     * @return role of author
     */
    public Role getRole() {
        return role;
    }

    @Override
    public String toString() {
        return "Login{" +
                "id=" + id +
                ", name=" + lastName +
                ", role="+ role +'\'' +
                '}';
    }

    /**
     * role of author setter
     * @param role role to be set
     */
    public void setRole(Role role) {
        Objects.requireNonNull(role);
        this.role = role;
    }
}
