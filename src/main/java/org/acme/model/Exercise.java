package org.acme.model;

import jakarta.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@NamedQuery(name = "Exercise.findAll", query = "SELECT e FROM Exercise e")
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String description;
    public String difficulty;
    @ElementCollection
    public List<String> concepts;
    @Lob
    public String signatureAndBody;
    @Lob
    public String unitTests;
    @Lob
    public String solution;

    public Exercise() {
        super();
    }

    public Exercise(String description) {
        Objects.requireNonNull(description);
        this.description = description;
    }

    @Override
    public String toString() {
        return "Exercise{" +
                "id=" + id +
                ", description='" + description + '\'' +
                '}';
    }
}
