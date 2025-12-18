package fr.uge.teacher.service;

import fr.uge.exercise.Exercise;
import fr.uge.exercise.exception.ExerciseUnauthorizedAccess;
import fr.uge.login.Login;
import fr.uge.login.Role;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class TeacherExerciseServiceTest {

    @Inject
    TeacherExerciseService teacherExerciseService;

    @Inject
    EntityManager entityManager;

    private Login testCreator;
    private Login otherUser;
    private Exercise testExercise;

    @BeforeEach
    @Transactional
    public void setup() {
        testCreator = new Login(
                "david",
                "starsky",
                Role.TEACHER
        );
        entityManager.persist(testCreator);

        otherUser = new Login(
                "kenneth",
                "hutch",
                Role.TEACHER
        );
        entityManager.persist(otherUser);

        testExercise = new Exercise();
        testExercise.setDescription("*description*");
        testExercise.setCreator(testCreator);
        entityManager.persist(testExercise);
        entityManager.flush();
    }

    @AfterEach
    @Transactional
    public void cleanup() {
        entityManager.createQuery("DELETE FROM Exercise").executeUpdate();
        entityManager.createQuery("DELETE FROM Login").executeUpdate();
    }

    @Test
    @DisplayName("should return exercise if ID is valid")
    public void testGetExercise_ValidId() {
        var result = teacherExerciseService.getExercise(testExercise.getId());
        assertNotNull(result);
        assertEquals(testExercise.getId(), result.getId());
        assertEquals("*description*", result.getDescription());
    }

    @Test
    @DisplayName("negative ID throws IllegalArgumentException")
    public void testGetExercise_NegativeId() {
        assertThrows(IllegalArgumentException.class, () -> {
            teacherExerciseService.getExercise(-1L);
        });
    }

    @Test
    @DisplayName("zero ID throws IllegalArgumentException")
    public void testGetExercise_ZeroId() {
        assertThrows(IllegalArgumentException.class, () -> {
            teacherExerciseService.getExercise(0L);
        });
    }

    @Test
    @DisplayName("non-existent ID throws NotFoundException")
    public void testGetExercise_NotFound() {
        assertThrows(NotFoundException.class, () -> {
            teacherExerciseService.getExercise(1991L);
        });
    }

    @Test
    @DisplayName("should return all exercises")
    @Transactional
    public void testGetAllExercises() {
        var exercise2 = new Exercise();
        exercise2.setDescription("*description2*");
        exercise2.setCreator(otherUser);
        entityManager.persist(exercise2);
        entityManager.flush();

        var result = teacherExerciseService.getAllExercises();
        assertTrue(result.size() >= 2);
        assertTrue(result.stream().anyMatch(e -> e.getDescription().equals("*description*")));
        assertTrue(result.stream().anyMatch(e -> e.getDescription().equals("*description2*")));
        assertTrue(result.stream().anyMatch(e -> e.getCreator().getId().equals(testCreator.getId())));
        assertTrue(result.stream().anyMatch(e -> e.getCreator().getId().equals(otherUser.getId())));
    }

    @Test
    @DisplayName("should return exercises created by a valid user")
    public void testGetExercisesByUserId_Valid() {
        var result = teacherExerciseService.getExercisesByUserId(testCreator.getId());
        assertFalse(result.isEmpty());
        assertTrue(result.stream().allMatch(e -> e.getCreator().getId().equals(testCreator.getId())));
    }

    @Test
    @DisplayName("should return nothing for user with no exercises")
    public void testGetExercisesByUserId_NoExercises() {
        var result = teacherExerciseService.getExercisesByUserId(otherUser.getId());
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("negative user ID throws IllegalArgumentException")
    public void testGetExercisesByUserId_NegativeId() {
        assertThrows(IllegalArgumentException.class, () -> {
            teacherExerciseService.getExercisesByUserId(-1L);
        });
    }

    @Test
    @DisplayName("zero user ID throws IllegalArgumentException")
    public void testGetExercisesByUserId_ZeroId() {
        assertThrows(IllegalArgumentException.class, () -> {
            teacherExerciseService.getExercisesByUserId(0L);
        });
    }

    @Test
    @DisplayName("deletion of exercise is successful")
    public void testDeleteExercise_Success() {
        assertDoesNotThrow(() -> {
            teacherExerciseService.deleteExercise(testExercise.getId(), testCreator.getId());
        });
        assertThrows(NotFoundException.class, () -> {
            teacherExerciseService.getExercise(testExercise.getId());
        });
    }

    @Test
    @DisplayName("attempt of deletion of an exercise not by the creator throws exception")
    public void testDeleteExercise_Unauthorized() {
        assertThrows(ExerciseUnauthorizedAccess.class, () -> {
            teacherExerciseService.deleteExercise(testExercise.getId(), otherUser.getId());
        });
        assertDoesNotThrow(() -> {
            teacherExerciseService.getExercise(testExercise.getId());
        });
    }

    @Test
    @DisplayName("deletion with negative exercise ID throws IllegalArgumentException")
    public void testDeleteExercise_NegativeExerciseId() {
        assertThrows(IllegalArgumentException.class, () -> {
            teacherExerciseService.deleteExercise(-1L, testCreator.getId());
        });
    }

    @Test
    @DisplayName("deletion with negative user ID throws IllegalArgumentException")
    public void testDeleteExercise_NegativeUserId() {
        assertThrows(IllegalArgumentException.class, () -> {
            teacherExerciseService.deleteExercise(testExercise.getId(), -1L);
        });
    }

    @Test
    @DisplayName("deletion with non-existent exercise throws NotFoundException")
    public void testDeleteExercise_ExerciseNotFound() {
        assertThrows(NotFoundException.class, () -> {
            teacherExerciseService.deleteExercise(1991L, testCreator.getId());
        });
    }

    @Test
    @DisplayName("exercise saving preserves creator information")
    public void testSaveModifiedExercise_PreservesCreator() {
        var newExercise = new Exercise();
        var result = teacherExerciseService.saveModifiedExercise(newExercise, testExercise.getId());
        assertNotNull(result);
        assertEquals(testExercise.getId(), result.getId());
        assertEquals(testCreator.getId(), result.getCreator().getId());
    }

    @Test
    @DisplayName("saving with null exercise throws NullPointerException")
    public void testSaveModifiedExercise_NullExercise() {
        assertThrows(NullPointerException.class, () -> {
            teacherExerciseService.saveModifiedExercise(null, testExercise.getId());
        });
    }

    @Test
    @DisplayName("exercise saving allows update of existing exercise")
    @Transactional
    public void testSaveModifiedExercise_UpdatesExercise() {
        var modifiedExercise = new Exercise();
        modifiedExercise.setDescription("Updated Description");

        teacherExerciseService.saveModifiedExercise(modifiedExercise, testExercise.getId());
        entityManager.flush();
        entityManager.clear();

        var retrieved = teacherExerciseService.getExercise(testExercise.getId());
        assertEquals("Updated Description", retrieved.getDescription());
    }

    @Test
    @DisplayName("Multiple exercises can be created by same user")
    @Transactional
    public void testMultipleExercisesSameUser() {
        var exercise2 = new Exercise();
        exercise2.setCreator(testCreator);
        entityManager.persist(exercise2);
        var exercise3 = new Exercise();
        exercise3.setCreator(testCreator);
        entityManager.persist(exercise3);

        entityManager.flush();
        var userExercises = teacherExerciseService.getExercisesByUserId(testCreator.getId());
        assertTrue(userExercises.size() >= 3);
    }
}