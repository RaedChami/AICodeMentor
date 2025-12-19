package fr.uge.teacher.service;

import fr.uge.exercise.Difficulty;
import fr.uge.exercise.Exercise;
import fr.uge.exercise.dto.ExerciseDTO;
import fr.uge.exercise.dto.UserPrompt;
import fr.uge.login.Login;
import fr.uge.login.LoginMapper;
import fr.uge.login.Role;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class TeacherGenerateServiceTest {

    @Inject
    TeacherGenerateService teacherGenerateService;

    @Inject
    EntityManager entityManager;

    private Login testTeacher;

    @BeforeEach
    @Transactional
    public void setup() {
        testTeacher = new Login(
                "doc",
                "brown",
                Role.TEACHER
        );
        entityManager.persist(testTeacher);
        entityManager.flush();
    }

    private ExerciseDTO exerciseDTOForTest() {
        return new ExerciseDTO(
                null,
                "description",
                Difficulty.L3,
                List.of("streams"),
                "signature",
                "tests",
                "solution",
                LoginMapper.convertToDTO(testTeacher)
        );
    }

    @AfterEach
    @Transactional
    public void cleanup() {
        entityManager.createQuery("DELETE FROM Exercise").executeUpdate();
        entityManager.createQuery("DELETE FROM Login").executeUpdate();
    }

    @Test
    @DisplayName("successfully persist valid exercise")
    public void testSaveGeneratedExercise_Success() throws NoSuchFieldException, IllegalAccessException {
        var result = teacherGenerateService.saveGeneratedExercise(exerciseDTOForTest());
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("description", result.getDescription());
        assertEquals(testTeacher.getId(), result.getCreator().getId());
    }

    @Test
    @DisplayName("saving null DTO throws NullPointerException")
    public void testSaveGeneratedExercise_NullDTO() {
        assertThrows(NullPointerException.class, () -> {
            teacherGenerateService.saveGeneratedExercise(null);
        });
    }

    @Test
    @DisplayName("sets creator correctly to exercise entity before saving")
    @Transactional
    public void testSaveGeneratedExercise_SetsCreator() throws NoSuchFieldException, IllegalAccessException {
        var result = teacherGenerateService.saveGeneratedExercise(exerciseDTOForTest());
        entityManager.flush();
        entityManager.clear();
        var retrieved = entityManager.find(Exercise.class, result.getId());
        assertNotNull(retrieved.getCreator());
        assertEquals(testTeacher.getId(), retrieved.getCreator().getId());
        assertEquals(testTeacher.getName(), retrieved.getCreator().getName());
    }

    @Test
    @DisplayName("null prompt throws NullPointerException")
    public void testGenerateExerciseService_NullPrompt() {
        assertThrows(NullPointerException.class, () -> {
            teacherGenerateService.generateExerciseService(null);
        });
    }

    @Test
    @DisplayName("generating with valid teacher ID uses existing teacher")
    public void testGenerateExerciseService_ValidTeacherId() {
        var prompt = new UserPrompt("Create a simple Java exercise", testTeacher.getId());
        assertDoesNotThrow(() -> {
            teacherGenerateService.generateExerciseService(prompt);
        });
    }

}