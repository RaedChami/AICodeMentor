package org.acme.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record Exercise(String description, String difficulty, List<String> concepts, String signatureAndBody, String UnitTests, String solution) {
    public Exercise {
        description = Objects.requireNonNull(description);
        difficulty = Objects.requireNonNull(difficulty);
        concepts = Objects.requireNonNull(concepts);
        signatureAndBody = Objects.requireNonNull(signatureAndBody);
        UnitTests = Objects.requireNonNull(UnitTests);
        solution = Objects.requireNonNull(solution);
    }
}
