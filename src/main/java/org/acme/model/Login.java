package org.acme.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@NamedQuery(name = "Login.findAll", query = "SELECT l FROM Login l")
public class Login {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String name;
    public String lastName;
    public Role role;
    enum Role {
        Student,
        Teacher
    }

    public Login() {}

    public Login(String name, String lastName, Role role) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(lastName);
        Objects.requireNonNull(role);
        this.name = name;
        this.lastName = lastName;
        this.role = role;
    }

    @Override
    public String toString() {
        return "Login{" +
                "id=" + id +
                ", name=" + lastName +
                ", role="+ role +'\'' +
                '}';
    }
}
