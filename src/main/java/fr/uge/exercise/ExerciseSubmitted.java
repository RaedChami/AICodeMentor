package fr.uge.exercise;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "JPA entity relationship must store the exact references")
    public ExerciseSubmitted(Login login, Exercise exercise, String solutionSubmitted) {
        this.login = Objects.requireNonNull(login);
        this.exercise = Objects.requireNonNull(exercise);
        this.solutionSubmitted = Objects.requireNonNull(solutionSubmitted);
    }

    public Long getId() {
        return id;
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "JPA entity relationship must return managed instance")
    public Login getLogin() {
        return login;
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "JPA entity relationship must return managed instance")
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
