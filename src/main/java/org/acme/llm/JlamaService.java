package org.acme.llm;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import dev.langchain4j.model.chat.ChatModel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.model.Exercise;

import java.io.IOException;
import java.util.Objects;

@ApplicationScoped
public class JlamaService {
    private static final ObjectReader reader = new ObjectMapper().reader();
    @Inject
    ChatModel model;

    public String ask(String prompt) {
        Objects.requireNonNull(prompt);
        return model.chat(prompt);
    }
    public Exercise generateExercise(String prompt) throws IOException {
        Objects.requireNonNull(prompt);
        var detailedPrompt = model.chat(
                "Pour le prompt suivant:" + prompt + "Génèrez un exercice au format JSON respectant les champs suivant:" +
                        "description: énoncé détaillé de l'exercice avec des exemples d'utilisation" +
                        "level: Un niveau académique recommandé pour l'exercice (L1,L2,L3,M1 ou M2) " +
                        "concepts: Liste des concepts pertinents avec l'exercice" +
                        "body: Signature de la fonction / Squelette de code" +
                        "tests: Tests unitaires JUnit pour l'exercice" +
                        "solution: Solution exemple pour l'exercice");
        try (JsonParser parser = reader.createParser(detailedPrompt)) {
            return parser.readValueAs(new TypeReference<Exercise>() {});
        }
    }

}
