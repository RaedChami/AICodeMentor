package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.model.Exercise;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import javax.tools.ToolProvider;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

@ApplicationScoped
public class ExerciseCompiler {

    @Inject
    ExerciseParser exerciseParser;

    private final static Path tmpDirectory = Paths.get("").toAbsolutePath().resolve("tmpDirectory");

    public boolean compile(String program) {
        Objects.requireNonNull(program);
        try {
            createTemporaryDirectory();
            var programPath = createTemporaryFiles(program);
            if (!compileCode(programPath)) {
                System.out.println("Compilation failed, Exercise regeneration");
                return false;
            }
            return true;
        } finally {
            cleanDirectory();
        }
    }

    public void createTemporaryDirectory() {
        try {
            Files.createDirectories(tmpDirectory);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    public Path createTemporaryFiles(String program) {
        Objects.requireNonNull(program);
        var fileDirectory = tmpDirectory.resolve(exerciseParser.getClassName(program));
        try {
            Files.writeString(fileDirectory, program);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        return fileDirectory;
    }

    public boolean compileCode(Path directory) {
        Objects.requireNonNull(directory);
        var compiler = ToolProvider.getSystemJavaCompiler();
        var result = compiler.run(null, null, null, directory.toString());
        System.out.println(result);
        return result == 0;
    }

    public void cleanDirectory() {
        var directory = tmpDirectory.toFile();
        var files = directory.listFiles();
        if (directory.exists()) {
            try {
                if (files != null) {
                    for (var file : files) {
                        Files.deleteIfExists(file.toPath());
                    }
                }
                Files.deleteIfExists(tmpDirectory);
            } catch (IOException e) {
                throw new AssertionError(e);
            }
        }
    }
    public String runUnitTests(String studentCode, String testCode) {
        createTemporaryDirectory();

        Path studentFile = tmpDirectory.resolve(exerciseParser.getClassName(studentCode));
        Path testFile = tmpDirectory.resolve(exerciseParser.getClassName(testCode));

        try {
            Files.writeString(studentFile, studentCode);
            Files.writeString(testFile, testCode);
        } catch (IOException e) {
            return "Erreur lors de l'écriture des fichiers : " + e.getMessage();
        }

        var compiler = ToolProvider.getSystemJavaCompiler();
        var compileResult = compiler.run(
                null, null, null,
                studentFile.toString(),
                testFile.toString()
        );

        if (compileResult != 0) {
            cleanDirectory();
            return "Erreur de compilation.\n";
        }

        try {
            URLClassLoader classLoader = new URLClassLoader(
                    new URL[]{ tmpDirectory.toUri().toURL() },
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

            cleanDirectory();
            return output.toString();

        } catch (Exception e) {
            cleanDirectory();
            return "Erreur lors de l'exécution des tests : " + e.getMessage();
        }

    }
}
