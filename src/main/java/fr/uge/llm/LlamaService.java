package fr.uge.llm;


import de.kherud.llama.LlamaModel;
import de.kherud.llama.ModelParameters;
import de.kherud.llama.InferenceParameters;

import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import fr.uge.exercise.service.ExerciseParser;
import fr.uge.exercise.Exercise;

import java.util.Objects;
import java.util.Optional;

@Startup
@ApplicationScoped
public class LlamaService {

    private final ExerciseParser parser;
    @Inject
    LlamaService(ExerciseParser parser) {
        this.parser = Objects.requireNonNull(parser);
    }

    private LlamaModel model;
    private final ModelParameters modelParams = new ModelParameters()
            .setModel("models/qwen2.5-coder-3b-instruct-q4_k_m.gguf")
            .setParallel(2);

    @PostConstruct
    void init() {
        this.model = new LlamaModel(modelParams);
    }

    @PreDestroy
    void destroy() {
        this.model.close();
    }

    /**
     * Requests local LLM and generates feedback to a student for its failed submission
     * @param studentCode program submitted by the student
     * @param exerciseTest JUnit test class for the corresponding exercise
     * @return A hint corresponding with the failed tests of a submission by a student
     */
    @SuppressFBWarnings("VA_FORMAT_STRING_USES_NEWLINE") // Suppress error caused by false positive in text blocks
    public String getHint(String studentCode, String exerciseTest) {
        Objects.requireNonNull(studentCode);
        Objects.requireNonNull(exerciseTest);
        var userMessage = """
                Tu es un assistant qui aide un étudiant en Java.

                Donne UN SEUL indice simple, clair et concis pour l’aider à corriger son code.
                Ne donne surtout pas la solution complète.
                Les tests unitaire de l'enseignant ne doivent pas être modifié.
                Les test unitaire de l'enseignant ne doivent surtout pas être montré à l'étudiant.

                Voici son code :
                %s

                Voici les tests unitaires fournis par son enseignant :
                %s
                """.formatted(studentCode, exerciseTest);
        var prompt = """
            <|im_start|>user
            %s
            <|im_end|>
            <|im_start|>assistant
            """.formatted(userMessage);
        var infer = new InferenceParameters(prompt)
                .setTemperature(0.7f)
                .setTopP(0.9f)
                .setNKeep(0);
        return model.complete(infer.setPrompt(prompt));
    }

    /**
     * Requests local LLM and Generates an exercise using prompt engineering
     * @param userDescription Initial prompt sent from the user
     * @return The proper exercise after cleanup from the initial response of the LLM
     */
    @SuppressFBWarnings("VA_FORMAT_STRING_USES_NEWLINE") // Suppress error caused by false positive in text blocks
    public Optional<Exercise> generateExercise(String userDescription) {
        Objects.requireNonNull(userDescription);
        var systemPrompt = getSystemPrompt();
        var userPrompt = String.format("""
        Créez un exercice de programmation Java complet pour : %s
        Respectez STRICTEMENT le format avec les balises demandées.
        """, userDescription);
        var fullPrompt = buildPrompt(systemPrompt, userPrompt);
        var inferParams = new InferenceParameters(fullPrompt)
                .setTemperature(0.1f)
                .setStopStrings("<|im_end|>")
                .setNPredict(2048);

        var answer = model.complete(inferParams);
        System.out.println(answer);
        return parser.parse(answer);
    }

    /**
     * Requests local LLM and Generates a partial modification of a given exercise
     * @param existingExercise Exercise to be modified
     * @param modificationDescription Initial prompt sent from the user
     * @return The proper exercise after cleanup from the initial response of the LLM
     */
    @SuppressFBWarnings("VA_FORMAT_STRING_USES_NEWLINE") // Suppress error caused by false positive in text blocks
    public Optional<Exercise> modifyExercise(Exercise existingExercise, String modificationDescription) {
        Objects.requireNonNull(existingExercise);
        Objects.requireNonNull(modificationDescription);

        var systemPrompt = getSystemModifyPrompt(existingExercise);
        var userPrompt = """
        Générez UNIQUEMENT la balise correspondant à la modification demandée.
        NE générez PAS les autres balises.
        Respectez les souhaits de modification de l'utilisateur : %s
        """.formatted(modificationDescription);

        var fullPrompt = buildPrompt(systemPrompt, userPrompt);
        var inferParams = new InferenceParameters(fullPrompt)
                .setTemperature(0.1f)
                .setStopStrings("<|im_end|>")
                .setNPredict(2048);

        var answer = model.complete(inferParams);
        System.out.println(answer);
        return parser.mergeExercise(existingExercise, answer);
    }

    /**
     * Returns the system prompt for the modification of an exercise
     * @param exercise Exercise to be modified
     * @return system prompt specific to a given exercise
     */
    @SuppressFBWarnings("VA_FORMAT_STRING_USES_NEWLINE") // Suppress error caused by false positive in text blocks
    private String getSystemModifyPrompt(Exercise exercise) {
        Objects.requireNonNull(exercise);
        return """
            Vous êtes un expert en modification d'exercices Java.
            
            RÈGLE ABSOLUE : Vous devez générer EXACTEMENT UNE SEULE balise parmi celles listées ci-dessous.
            Ne générez JAMAIS plusieurs balises dans la même réponse.
            Ne générez AUCUN texte avant ou après la balise demandée.
            
            Balises disponibles (choisissez UNE SEULE) :
            
            1. Pour modifier l'énoncé actuel:
            <ENONCE>
            %s
            </ENONCE>
            
            2. Pour modifier la difficulté actuelle :
            <DIFFICULTE>
            %s
            </DIFFICULTE>
            
            3. Pour modifier les concepts actuels:
            <CONCEPTS>
            %s
            </CONCEPTS>
            
            4. Pour modifier la signature actuelle :
            <SIGNATURE>
            %s
            </SIGNATURE>
            
            5. Pour modifier les tests actuelles :
            <TESTS>
            %s
            </TESTS>
            
            6. Pour modifier la solution actuelle :
            <SOLUTION>
            %s
            </SOLUTION>
            
            IMPORTANT : Générez UNIQUEMENT la balise correspondant à la demande de l'utilisateur.
            """.formatted(
                exercise.getDescription(),
                exercise.getDifficulty(),
                exercise.getConcepts(),
                exercise.getSignatureAndBody(),
                exercise.getUnitTests(),
                exercise.getSolution()
        );
    }

    /**
     * Returns the system prompt for the generation of an exercise
     * @return system prompt for generating an exercise
     */
    private String getSystemPrompt() {
        return """
                Vous êtes un expert en création d'exercices de programmation Java.
                Vous ne confondez pas les ARRAYLISTS et les ARRAYS.
                La balise <SOLUTION> doit toujours se fermer.
                Vérifiez la COHÉRENCE entre signature, tests et solution
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
                EXEMPLE:
                public class Solution {
                    public String printHelloWorld() {
                        // TODO
                    }
                }
                </SIGNATURE>
                
                <TESTS>
                Écrivez une classe contenant les tests JUnit 5 complets sans oublier les imports de librairie.
                EXEMPLE:
                import org.junit.jupiter.api.Test;
                import static org.junit.jupiter.api.Assertions.*;
                import java.util.*;
                public class SolutionTest {
                    @Test
                    public void test1() {
                        Solution solution = new Solution();
                        // TODO
                    }
                }
                </TESTS>
                
                <SOLUTION>
                Écrivez une classe contenant solution complète de l'exercice.
                EXEMPLE:
                import java.util.*;
                public class Solution {
                    // TODO
                }
                </SOLUTION>
                
                """;
    }

    /**
     * Returns the prompt template specific to Qwen
     * @param systemPrompt system prompt content
     * @param userPrompt user prompt content
     * @return The full prompt integrated to a template
     */
    @SuppressFBWarnings("VA_FORMAT_STRING_USES_NEWLINE") // Suppress error caused by false positive in text blocks
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