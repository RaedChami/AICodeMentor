package org.acme.llm;


import de.kherud.llama.LlamaModel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.model.Exercise;
import org.acme.resource.ExerciseParser;

import de.kherud.llama.ModelParameters;
import de.kherud.llama.args.MiroStat;
import de.kherud.llama.InferenceParameters;
import java.util.Objects;

@ApplicationScoped
public class JlamaService {
    private LlamaModel model;

    @Inject
    ExerciseParser parser;
    public Exercise generateExercise(String userDescription) {
        Objects.requireNonNull(userDescription);

        var modelParams = new ModelParameters()
                .setModel("models/qwen2.5-coder-3b-instruct-q4_0.gguf")
                .setGpuLayers(43);
        model = new LlamaModel(modelParams);
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
        var inferParams = new InferenceParameters(fullPrompt)
                .setTemperature(0.7f)
                .setPenalizeNl(true)
                .setMiroStat(MiroStat.V2)
                .setStopStrings("User:");

        var answer = model.complete(inferParams);
        return parser.parse(answer);
    }

}
