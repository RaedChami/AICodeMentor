package fr.uge.student;

import fr.uge.exercise.service.ExerciseRunJUnitTest;

import java.io.IOException;
import java.nio.file.Path;

public class SubProcessMain {

    public static void main(String args[]) throws IOException {

        var testClassName = args[0];
        var studentFile = Path.of(args[1]);
        var testFile = Path.of(args[2]);

        ExerciseRunJUnitTest exerciseRunJUnitTest = new ExerciseRunJUnitTest();

        System.out.println(exerciseRunJUnitTest.runUnitTests(
                testClassName,
                studentFile,
                testFile
        ));
    }
}
