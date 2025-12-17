package fr.uge.exercise;

import fr.uge.login.Login;
import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

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
    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = true)
    private Login creator;

    /**
     * Default constructor for exercise entity
     */
    public Exercise() {
        super();
    }

    public Exercise(String description, Difficulty difficulty, List<String> concepts, String signatureAndBody, String unitTests, String solution) {
        Objects.requireNonNull(concepts);
        this.description = Objects.requireNonNull(description);
        this.difficulty = Objects.requireNonNull(difficulty);;
        this.concepts = new ArrayList<>(Objects.requireNonNull(concepts));
        this.signatureAndBody = Objects.requireNonNull(signatureAndBody);
        this.unitTests = Objects.requireNonNull(unitTests);
        this.solution = Objects.requireNonNull(solution);
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

    public Login getCreator() {
        return creator;
    }

    public void setCreator(Login creator) {
        this.creator = creator;
    }

    public void setId(Long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("id is inferior or equal to zero");
        }
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = Objects.requireNonNull(description);
    }

    public void setTests(String tests) {
        this.unitTests = Objects.requireNonNull(tests);
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = Objects.requireNonNull(difficulty);
    }

    public void setConcepts(List<String> concepts) {
        this.concepts = new ArrayList<>(Objects.requireNonNull(concepts));
    }

    public void setSignatureAndBody(String signatureAndBody) {
        this.signatureAndBody = Objects.requireNonNull(signatureAndBody);
    }

    public void setSolution(String solution) {
        this.solution = Objects.requireNonNull(solution);
    }
}
