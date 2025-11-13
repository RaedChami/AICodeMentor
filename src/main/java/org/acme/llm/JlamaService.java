package org.acme.llm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import dev.langchain4j.model.chat.ChatModel;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.model.Exercise;
import org.acme.resource.ExerciseParser;

import java.util.Objects;

@ApplicationScoped
public class JlamaService {
    private static final ObjectReader reader = new ObjectMapper().reader();
    @Inject
    ChatModel model;
    @Inject
    ExerciseParser parser;

    public String ask(String prompt) {
        Objects.requireNonNull(prompt);
        return model.chat(prompt);
    }
    public Exercise generateExercise(String userDescription) {
        Objects.requireNonNull(userDescription);

        var systemPrompt = """
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
            Écrivez la signature des méthode/classe Java à compléter (ne pas inclure de code entier à l'exception des noms de méthodes/classe)
            </SIGNATURE>
            
            <TESTS>
            Écrivez les tests JUnit 5 complets sans oublier les imports de librairie
            </TESTS>
            
            <SOLUTION>
            Écrivez la solution complète de l'exercice sans oublier les éventuels imports de librairie
            </SOLUTION>
            
            """;

        var userPrompt = String.format("""
            Créez un exercice de programmation Java complet pour : %s
            
            Respectez STRICTEMENT le format avec les balises demandées.
            """, userDescription);

        var fullPrompt = String.format(
                "<|begin_of_text|><|start_header_id|>system<|end_header_id|>%s<|eot_id|>" +
                        "<|start_header_id|>user<|end_header_id|>%s<|eot_id|>" +
                        "<|start_header_id|>assistant<|end_header_id|>",
                systemPrompt, userPrompt
        );
        var answer = model.chat(fullPrompt);
        System.out.println(answer);
        return parser.parse(answer);
    }

}
