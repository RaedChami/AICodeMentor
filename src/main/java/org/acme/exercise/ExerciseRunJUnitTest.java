package org.acme.exercise;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import javax.tools.ToolProvider;

import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

@ApplicationScoped
public class ExerciseRunJUnitTest {
    @Inject
    ExerciseParser exerciseParser;
    @Inject
    ExerciseCompiler exerciseCompiler;
    private final static Path tmpDirectory = Paths.get("").toAbsolutePath().resolve("tmpDirectory");
    public String runUnitTests(String studentCode, String testCode) throws IOException {
        Objects.requireNonNull(studentCode);
        Objects.requireNonNull(testCode);
        try {
            URLClassLoader classLoader = new URLClassLoader(
                    new URL[]{tmpDirectory.toUri().toURL()},
                    this.getClass().getClassLoader()
            );

            Thread.currentThread().setContextClassLoader(classLoader);

            String testClassName = exerciseParser.getClassName(testCode).replace(".java", "");

            var launcher = LauncherFactory.create();

            var request = LauncherDiscoveryRequestBuilder.request()
                    .selectors(DiscoverySelectors.selectClass(testClassName))
                    .build();

            var listener = new SummaryGeneratingListener();
            launcher.registerTestExecutionListeners(listener);

            launcher.execute(request);

            var summary = listener.getSummary();
            StringBuilder output = new StringBuilder();

            output.append("Tests exécutés : ").append(summary.getTestsFoundCount()).append("\n");
            output.append("Succès : ").append(summary.getTestsSucceededCount()).append("\n");
            output.append("Échecs : ").append(summary.getTestsFailedCount()).append("\n\n");

            summary.getFailures().forEach(failure -> {
                output.append("❌ ").append(failure.getTestIdentifier().getDisplayName()).append("\n");
                output.append(failure.getException().getMessage()).append("\n\n");
            });

            exerciseCompiler.cleanDirectory();
            return output.toString();

        } catch (Exception e) {
            exerciseCompiler.cleanDirectory();
            return "Erreur lors de l'exécution des tests : " + e.getMessage();
        }

    }
}