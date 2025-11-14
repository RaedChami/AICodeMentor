package org.acme.resource;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.model.Exercise;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@ApplicationScoped
public class ExerciseParser {
    public Exercise parse(String answer) {
        Objects.requireNonNull(answer);
        var exercise = new Exercise();
        exercise.description = matchPattern(answer, Pattern.compile("<ENONCE>(.*)</ENONCE>", Pattern.DOTALL));
        exercise.difficulty = matchPattern(answer, Pattern.compile("<DIFFICULTE>(.*)</DIFFICULTE>", Pattern.DOTALL));
        exercise.concepts = stockConcepts(answer, Pattern.compile("<CONCEPTS>(.*)</CONCEPTS>", Pattern.DOTALL));
        exercise.signatureAndBody = matchPattern(answer, Pattern.compile("<SIGNATURE>(.*)</SIGNATURE>", Pattern.DOTALL));
        exercise.unitTests = matchPattern(answer, Pattern.compile(
                "<TESTS>\\s*(?:```(?:java)?\\s*)?([\\s\\S]*?)(?:```\\s*)?(?=</TESTS>|$)", Pattern.DOTALL));
        exercise.solution = matchPattern(answer, Pattern.compile(
                "<SOLUTION>*>\\s*(?:```(?:java)?\\s*)?([\\s\\S]*?)(?:```\\s*)?(?=</SOLUTION>|$)",
                Pattern.DOTALL | Pattern.CASE_INSENSITIVE));
        return exercise;
    }

    private String matchPattern(String answer, Pattern pattern) {
        Objects.requireNonNull(answer);
        Objects.requireNonNull(pattern);
        var matcher = pattern.matcher(answer);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "";
    }

    private List<String> stockConcepts(String answer, Pattern pattern) {
        Objects.requireNonNull(answer);
        Objects.requireNonNull(pattern);
        var matcher = matchPattern(answer, pattern);
        var words = matcher.split(",");
        return Arrays.stream(words).toList();
    }

    public String getClassName(String code) {
        Objects.requireNonNull(code);
        return matchPattern(code, Pattern.compile("class (.*)\\{")) + ".java";
    }

}
