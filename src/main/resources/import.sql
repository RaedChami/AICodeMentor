INSERT INTO Exercise (id, description, difficulty, signatureAndBody, unitTests, solution)
VALUES (
    1,
    'Implémentez une fonction en Java qui retourne le deuxième plus grand nombre dans un tableau d''entiers.',
    1,
    'public class SecondLargestFinder {
    public static int findSecondLargest(int[] numbers) {
        // TODO: implémentez cette méthode
        return 0;
    }
}',
    'import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SecondLargestFinderTest {

    @Test
    public void testNormalCase() {
        int[] arr = {1, 5, 3, 9, 7};
        assertEquals(7, SecondLargestFinder.findSecondLargest(arr));
    }

    @Test
    public void testWithDuplicates() {
        int[] arr = {4, 4, 2, 9, 9};
        assertEquals(4, SecondLargestFinder.findSecondLargest(arr));
    }

    @Test
    public void testNegativeNumbers() {
        int[] arr = {-10, -3, -20, -4};
        assertEquals(-4, SecondLargestFinder.findSecondLargest(arr));
    }

    @Test
    public void testTooSmall() {
        int[] arr = {5};
        assertThrows(IllegalArgumentException.class, () -> {
            SecondLargestFinder.findSecondLargest(arr);
        });
    }
}',
    'public class SecondLargestFinder {
    public static int findSecondLargest(int[] numbers) {
        if (numbers == null || numbers.length < 2) {
            throw new IllegalArgumentException("Array must contain at least two elements");
        }

        Integer largest = null;
        Integer second = null;

        for (int n : numbers) {
            if (largest == null || n > largest) {
                second = largest;
                largest = n;
            } else if (n != largest && (second == null || n > second)) {
                second = n;
            }
        }

        if (second == null) {
            throw new IllegalArgumentException("No second largest number found");
        }

        return second;
    }
}'
);
