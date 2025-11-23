package org.acme.service;

import de.kherud.llama.LlamaModel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.model.Exercise;

import de.kherud.llama.ModelParameters;
import de.kherud.llama.InferenceParameters;
import java.util.Objects;

@ApplicationScoped
public class LlamaService {

    @Inject
    ExerciseParser parser;

    public Exercise generateExercise(String userDescription) {
        Objects.requireNonNull(userDescription);
        var modelParams = new ModelParameters()
                .setModel("models/qwen2.5-coder-3b-instruct-q4_k_m.gguf")
                .setGpuLayers(0);
        try (var model = new LlamaModel(modelParams)) {
            var systemPrompt = getSystemPrompt();
            var userPrompt = String.format("""
            Créez un exercice de programmation Java complet pour : %s
            Respectez STRICTEMENT le format avec les balises demandées.
            """, userDescription);
            var fullPrompt = buildPrompt(systemPrompt, userPrompt);
            var inferParams = new InferenceParameters(fullPrompt)
                    .setTemperature(0.2f)
                    .setStopStrings("<|im_end|>")
                    .setNPredict(600);
            var answer = model.complete(inferParams);
            return parser.parse(answer);
        }
    }

    public Exercise modifyExercise(Exercise existingExercise, String modificationDescription) {
        Objects.requireNonNull(existingExercise);
        Objects.requireNonNull(modificationDescription);

        var modelParams = new ModelParameters()
                .setModel("models/qwen2.5-coder-3b-instruct-q4_k_m.gguf")
                .setGpuLayers(0);
        try (var model = new LlamaModel(modelParams)) {
            var systemPrompt = getSystemPrompt();

            var userPrompt = String.format("""
            Voici un exercice existant :
            
            Énoncé actuel : %s
            Difficulté actuelle : %s
            Concepts actuels : %s
            Signature actuelle : %s
            Tests actuelle : %s
            Solution actuelle : %s
            
            Instructions de modification : %s
            
            Générez la version COMPLÈTE et MODIFIÉE de l'exercice en respectant STRICTEMENT le format avec les balises demandées.
            Appliquez UNIQUEMENT les modifications demandées tout en conservant la cohérence de l'exercice.
            Ne modifiez pas du contenu non-demandée
            """,
                    existingExercise.getDescription(),
                    existingExercise.getDifficulty(),
                    existingExercise.getConcepts(),
                    existingExercise.getSignatureAndBody(),
                    existingExercise.getUnitTests(),
                    existingExercise.getSolution(),
                    modificationDescription);

            var fullPrompt = buildPrompt(systemPrompt, userPrompt);
            var inferParams = new InferenceParameters(fullPrompt)
                    .setTemperature(0.2f)
                    .setStopStrings("<|im_end|>")
                    .setNPredict(600);
            var answer = model.complete(inferParams);
            return parser.parse(answer);
        }
    }

    private String getSystemPrompt() {
        return """
            Vous êtes un expert en création d'exercices de programmation Java.
            Vous ne confondez pas les LISTES et les TABLEAUX.
            La balise <SOLUTION> doit toujours se fermer.
            Vous devez OBLIGATOIREMENT et TOUJOURS structurer votre réponse avec ces balises exactes :
            
            <ENONCE>
            Rédigez un énoncé avec TOUJOURS des exemples d'entrée/sortie (ne pas inclure de code)
            </ENONCE>
            
            <DIFFICULTE>
            Indiquez seulement le niveau académique de l'exercice : L1 ou L2 ou L3 ou M1 ou M2
            </DIFFICULTE>
            
            <CONCEPTS>
            Listez les concepts à utiliser pour l'exercice, séparés par des virgules (ex: boucles, tableaux, récursivité)
            </CONCEPTS>
            
            <SIGNATURE>
            Écrivez la signature de la méthode à implémenter pour l'exercice (ne pas inclure de code entier à l'exception des noms de méthodes/classe)
            -Exemple de structure à respecter STRICTEMENT:
            public String printHelloWorld()
            </SIGNATURE>
            
            <TESTS>
            Écrivez une classe contenant les tests JUnit 5 complets sans oublier les imports de librairie.
            -Structure à respecter STRICTEMENT:
            package com.exercice.solution;
            
            import org.junit.jupiter.api.Test;
            import static org.junit.jupiter.api.Assertions.*;
            
            public class SolutionTest {
                @Test
                public void test1() {
                    // TODO
                }
            }
            </TESTS>
            
            <SOLUTION>
            Écrivez une classe contenant solution complète de l'exercice sans oublier les éventuels imports de librairie.
            -Structure à respecter STRICTEMENT:
            package com.exercice.solution;
            
            public class Solution {
                // TODO
            }
            </SOLUTION>
            """;
    }

    private String buildPrompt(String systemPrompt, String userPrompt) {
        Objects.requireNonNull(systemPrompt);
        Objects.requireNonNull(userPrompt);
        return """
            <|im_start|>system
            %s
            <|im_end|>
            <|im_start|>user
            %s
            <|im_end|>
            <|im_start|>assistant
            """.formatted(systemPrompt, userPrompt);
    }
}