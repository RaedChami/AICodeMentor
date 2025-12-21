package fr.uge.exercise;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import fr.uge.login.Login;
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
        this.difficulty = Objects.requireNonNull(difficulty);
        this.concepts = new ArrayList<>(Objects.requireNonNull(concepts));
        this.signatureAndBody = Objects.requireNonNull(signatureAndBody);
        this.unitTests = Objects.requireNonNull(unitTests);
        this.solution = Objects.requireNonNull(solution);
    }

    /**
     * exercise ID getter
     * @return id of an exercise
     */
    public Long getId() {
        return id;
    }

    /**
     * description getter of an exercise
     * @return description of exercise
     */
    public String getDescription() {
        return description;
    }

    /**
     * difficulty getter of an exercise
     * @return difficulty of exercise
     */
    public Difficulty getDifficulty() {
        return difficulty;
    }

    /**
     * concepts getter of an exercise
     * @return list of concepts of exercise
     */
    public List<String> getConcepts() {
        return new ArrayList<>(concepts);
    }

    /**
     * signature getter of an exercise
     * @return signature and body of an exercise
     */
    public String getSignatureAndBody() {
        return signatureAndBody;
    }

    /**
     * JUnit program getter of exercise
     * @return JUnit tests of exercise
     */
    public String getUnitTests() {
        return unitTests;
    }

    /**
     * solution program getter of exercise
     * @return solution program of exercise
     */
    public String getSolution() {
        return solution;
    }

    /**
     * author of exercise getter
     * @return author of exercise
     */
    @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "JPA entity relationship must return managed instance")
    public Login getCreator() {
        return creator;
    }

    /**
     * author of exercise setter
     * @param creator author to be set
     */
    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "JPA entity relationship must store the exact reference")
    public void setCreator(Login creator) {
        this.creator = creator;
    }

    /**
     * id of exercise setter
     * @param id ID to be set
     */
    public void setId(Long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("id is inferior or equal to zero");
        }
        this.id = id;
    }

    /**
     * description of exercise setter
     * @param description description to be set
     */
    public void setDescription(String description) {
        this.description = Objects.requireNonNull(description);
    }

    /**
     * JUnit tests of exercise setter
     * @param tests JUnit tests to be set
     */
    public void setTests(String tests) {
        this.unitTests = Objects.requireNonNull(tests);
    }

    /**
     * Difficulty of exercise setter
     * @param difficulty difficulty to be set
     */
    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = Objects.requireNonNull(difficulty);
    }

    /**
     * Concepts of exercise setter
     * @param concepts concepts of exercise to be set
     */
    public void setConcepts(List<String> concepts) {
        this.concepts = new ArrayList<>(Objects.requireNonNull(concepts));
    }

    /**
     * Signature & Body of exercise setter
     * @param signatureAndBody signature & body to be set
     */
    public void setSignatureAndBody(String signatureAndBody) {
        this.signatureAndBody = Objects.requireNonNull(signatureAndBody);
    }

    /**
     * Solution of exercise setter
     * @param solution solution to be set
     */
    public void setSolution(String solution) {
        this.solution = Objects.requireNonNull(solution);
    }
}
