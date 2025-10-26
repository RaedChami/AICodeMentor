package org.acme.llm;

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
    public Exercise generateExercise(String prompt) {
        Objects.requireNonNull(prompt);
        var answer = model.chat("<|begin_of_text|>" +
                "<|start_header_id|>system<|end_header_id|>" +
                "Vous êtes un expert en création d'énoncé d'exercices de programmation" +
                "<|eot_id|>" +
                "<|start_header_id|>user<|end_header_id|>" +
                "Votre réponse ne doit pas contenir de code. Retournez un énoncé détaillé décrivant la simple description suivante"
                        + prompt +
                "<|eot_id|><|start_header_id|>assistant<|end_header_id|>"
                );
        var generatedExercise = new Exercise();
        generatedExercise.description = answer;
        return generatedExercise;
    }

}
