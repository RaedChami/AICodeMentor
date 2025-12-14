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
INSERT INTO Exercise (id, description, difficulty, signatureAndBody, unitTests, solution)
VALUES (
    2,
    'Créer un exercice où l''on trie une liste par dichotomie.',
    2,
    'public class BinaryInsertionSort {
    public static void sort(int[] array) {
        // TODO: implémentez le tri par insertion avec recherche dichotomique
    }
}',
    'import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BinaryInsertionSortTest {

    @Test
    public void testAlreadySorted() {
        int[] arr = {1, 2, 3, 4, 5};
        BinaryInsertionSort.sort(arr);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, arr);
    }

    @Test
    public void testUnsortedArray() {
        int[] arr = {5, 2, 4, 6, 1, 3};
        BinaryInsertionSort.sort(arr);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5, 6}, arr);
    }

    @Test
    public void testWithDuplicates() {
        int[] arr = {4, 2, 4, 3, 1};
        BinaryInsertionSort.sort(arr);
        assertArrayEquals(new int[]{1, 2, 3, 4, 4}, arr);
    }

    @Test
    public void testEmptyArray() {
        int[] arr = {};
        BinaryInsertionSort.sort(arr);
        assertArrayEquals(new int[]{}, arr);
    }
}',
    'public class BinaryInsertionSort {

    public static void sort(int[] array) {
        if (array == null || array.length < 2) {
            return;
        }

        for (int i = 1; i < array.length; i++) {
            int key = array[i];
            int left = 0;
            int right = i - 1;

            // Recherche dichotomique de la position d''insertion
            while (left <= right) {
                int mid = (left + right) / 2;
                if (array[mid] > key) {
                    right = mid - 1;
                } else {
                    left = mid + 1;
                }
            }

            // Décalage des éléments
            for (int j = i - 1; j >= left; j--) {
                array[j + 1] = array[j];
            }

            array[left] = key;
        }
    }
}'
);

