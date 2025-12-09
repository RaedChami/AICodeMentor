package org.acme.exercise;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Access(AccessType.FIELD)
@Entity
@NamedQuery(name = "Exercise.findAll", query = "SELECT e FROM Exercise e")
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
    private Difficulty difficulty;
    @ElementCollection
    private List<String> concepts;
    @Lob
    private String signatureAndBody;
    @Lob
    private String unitTests;
    @Lob
    private String solution;

    public Exercise() {
        super();
    }

    public Exercise(String description, Difficulty difficulty, List<String> concepts, String signatureAndBody, String unitTests, String solution) {
        Objects.requireNonNull(description);
        Objects.requireNonNull(difficulty);
        Objects.requireNonNull(concepts);
        Objects.requireNonNull(signatureAndBody);
        Objects.requireNonNull(unitTests);
        Objects.requireNonNull(solution);
        this.description = description;
        this.difficulty = difficulty;
        this.concepts = new ArrayList<>(concepts);
        this.signatureAndBody = signatureAndBody;
        this.unitTests = unitTests;
        this.solution = solution;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public List<String> getConcepts() {
        return new ArrayList<>(concepts);
    }

    public String getSignatureAndBody() {
        return signatureAndBody;
    }

    public String getUnitTests() {
        return unitTests;
    }

    public String getSolution() {
        return solution;
    }

    public void setId(Long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("id is inferior or equal to zero");
        }
        this.id = id;
    }
}
