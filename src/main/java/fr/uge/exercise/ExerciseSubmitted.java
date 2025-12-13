package fr.uge.exercise;

import fr.uge.login.Login;
import jakarta.persistence.*;

import java.util.Objects;

@Access(AccessType.FIELD)
@Entity
@NamedQueries({
        @NamedQuery(
                name = "ExerciseSubmitted.findExercisesByLoginId",
                query = """
            SELECT es.exercise
            FROM ExerciseSubmitted es
            WHERE es.login.id = :loginId
        """
        ),
        @NamedQuery(
                name = "ExerciseSubmitted.findByLoginAndExercise",
                query = """
            SELECT es
            FROM ExerciseSubmitted es
            WHERE es.login.id = :loginId
              AND es.exercise.id = :exerciseId
        """
        )
})
public class ExerciseSubmitted {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "login_id", nullable = false)
    private Login login;

    @ManyToOne(optional = false)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @Lob
    @Column(nullable = false)
    private String solutionSubmitted;

    public ExerciseSubmitted() {
    }

    public ExerciseSubmitted(Login login, Exercise exercise, String solutionSubmitted) {
        this.login = Objects.requireNonNull(login);
        this.exercise = Objects.requireNonNull(exercise);
        this.solutionSubmitted = Objects.requireNonNull(solutionSubmitted);
    }

    public Long getId() {
        return id;
    }

    public Login getLogin() {
        return login;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public String getSolutionSubmitted() {
        return solutionSubmitted;
    }

    public void setSolutionSubmitted(String solutionSubmitted) {
        this.solutionSubmitted = Objects.requireNonNull(solutionSubmitted);
    }
}
