package org.acme.login;

import jakarta.persistence.*;

import java.util.Objects;

import static jakarta.persistence.GenerationType.SEQUENCE;

@Entity
@NamedQuery(name = "Login.findAll", query = "SELECT l FROM Login l")
public class Login {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String lastName;
    private Role role;

    public Login() {}

    public Login(String name, String lastName, Role role) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(lastName);
        Objects.requireNonNull(role);
        this.name = name;
        this.lastName = lastName;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

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

    public void setRole(Role role) {
        Objects.requireNonNull(role);
        this.role = role;
    }
}
