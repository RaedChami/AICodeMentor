package fr.uge.exercise.service;


import jakarta.enterprise.context.ApplicationScoped;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
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

    ExerciseParser exerciseParser = new ExerciseParser();
    ExerciseCompiler exerciseCompiler = new ExerciseCompiler(exerciseParser);
    private final static Path tmpDirectory = Paths.get("").toAbsolutePath().resolve("tmpDirectory");

    private String getOutputSummary(SummaryGeneratingListener listener) {
        var summary = listener.getSummary();
        var output = new StringBuilder();

        output.append("Tests exécutés : ").append(summary.getTestsFoundCount()).append("\n");
        output.append("Succès : ").append(summary.getTestsSucceededCount()).append("\n");
        output.append("Échecs : ").append(summary.getTestsFailedCount()).append("\n\n");

        summary.getFailures().forEach(failure -> {
            output.append("❌ ").append(failure.getTestIdentifier().getDisplayName()).append("\n");
            output.append(failure.getException().getMessage()).append("\n\n");
        });
        return output.toString();
    }

    private String getTestOutput(String testClassName) throws IOException {
        try {
            URLClassLoader classLoader = new URLClassLoader(
                    new URL[]{tmpDirectory.toUri().toURL()},
                    this.getClass().getClassLoader()
            );
            Thread.currentThread().setContextClassLoader(classLoader);
            var launcher = LauncherFactory.create();
            var request = LauncherDiscoveryRequestBuilder.request()
                    .selectors(DiscoverySelectors.selectClass(testClassName))
                    .build();

            var listener = new SummaryGeneratingListener();
            launcher.registerTestExecutionListeners(listener);
            launcher.execute(request);
            return getOutputSummary(listener);
        } catch (Exception e) {
            exerciseCompiler.cleanDirectory();
            return "Erreur lors de l'exécution des tests : " + e.getMessage();
        }
    }


    public String runUnitTests(String testClassName, Path studentFile, Path testFile) throws IOException {
        Objects.requireNonNull(testClassName);
        Objects.requireNonNull(studentFile);
        Objects.requireNonNull(testFile);
        var compiler = ToolProvider.getSystemJavaCompiler();
        var compileResult = compiler.run(
                null, null, null,
                studentFile.toString(),
                testFile.toString()
        );
        if (compileResult != 0) {
            exerciseCompiler.cleanDirectory();
            return "Erreur de compilation.\n";
        }
        var output = getTestOutput(testClassName);
        exerciseCompiler.cleanDirectory();
        return output;
    }
}