package org.acme.exercise.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.exercise.Difficulty;
import org.acme.exercise.Exercise;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

@ApplicationScoped
public class ExerciseParser {
    private final static Pattern classPattern = Pattern.compile("class (.*)\\{");
    private final static Pattern enoncePattern = Pattern.compile("<ENONCE>(.*)</ENONCE>", Pattern.DOTALL);
    private final static Pattern difficultyPattern = Pattern.compile("<DIFFICULTE>(.*)</DIFFICULTE>", Pattern.DOTALL);
    private final static Pattern conceptsPattern = Pattern.compile("<CONCEPTS>(.*)</CONCEPTS>", Pattern.DOTALL);
    private final static Pattern signatureBodyPattern = Pattern.compile("<SIGNATURE>(.*)</SIGNATURE>", Pattern.DOTALL);
    private final static Pattern testsPattern = Pattern.compile("<TESTS>(.*)</TESTS>", Pattern.DOTALL);
    private final static Pattern solutionPattern = Pattern.compile("<SOLUTION>(.*)</SOLUTION>",
            Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

    /**
     * Parses the answer of the LLM
     * @param answer LLM exercise response
     * @return Parsed exercise
     */
    public Optional<Exercise> parse(String answer) {
        Objects.requireNonNull(answer);
        var description = matchPattern(answer, enoncePattern);
        var difficulty = matchDifficultyPattern(answer);
        var concepts = stockConcepts(answer);
        var signatureAndBody = matchPattern(answer, signatureBodyPattern);
        var unitTests = matchPattern(answer, testsPattern);
        var solution = matchPattern(answer, solutionPattern);

        if (description.isEmpty() || difficulty.isEmpty() || concepts.isEmpty() || signatureAndBody.isEmpty()
                || unitTests.isEmpty() || solution.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new Exercise(
                description.orElseThrow(),
                difficulty.orElseThrow(),
                concepts.orElseThrow(),
                signatureAndBody.orElseThrow(),
                unitTests.orElseThrow(),
                solution.orElseThrow()
        ));
    }

    private Optional<Difficulty> matchDifficultyPattern(String answer) {
        Objects.requireNonNull(answer);
        var checkDifficulty = matchPattern(answer, difficultyPattern);
        if (checkDifficulty.isPresent()) {
            return Optional.of(Difficulty.valueOf(checkDifficulty.orElseThrow().toUpperCase().trim()));
        }
        return Optional.empty();
    }

    private Optional<String> matchPattern(String answer, Pattern pattern) {
        Objects.requireNonNull(answer);
        Objects.requireNonNull(pattern);
        var matcher = pattern.matcher(answer);
        if (matcher.find()) {
            return Optional.of(matcher.group(1).trim());
        }
        return Optional.empty();
    }

    private Optional<List<String>> stockConcepts(String answer) {
        Objects.requireNonNull(answer);
        Objects.requireNonNull(ExerciseParser.conceptsPattern);
        var matcher = matchPattern(answer, ExerciseParser.conceptsPattern);
        if (matcher.isEmpty()) {
            return Optional.empty();
        }
        var words = matcher.get().split(",");
        var concepts = Arrays.stream(words).map(String::trim).toList();
        return Optional.of(concepts);
    }

    public String getClassName(String code) {
        Objects.requireNonNull(code);
        return matchPattern(code, classPattern).map(name -> name + ".java").orElseThrow();
    }

}
