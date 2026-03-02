package lab1.task1.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TrigonometricTest {

    // Допустимая погрешность. Для рядов Тейлора 0.0001 - разумный компромисс.
    private static final double DELTA = 0.0001;

    @ParameterizedTest(name = "arccos({0})")
    @DisplayName("Check corner and invalid values")
    @ValueSource(doubles = {
            -1.0000001, // Out of range
            -1.0,       // Boundary
            -0.999,     // Near boundary
            -0.5,
            0.0,
            0.5,
            0.999,      // Near boundary
            1.0,        // Boundary
            1.0000001,  // Out of range
            Double.NaN,
            Double.POSITIVE_INFINITY
    })
    void checkCornerDots(double param) {
        assertAll(
                () -> assertEquals(Math.acos(param), Trigonometric.arccos(param), DELTA)
        );
    }

    @ParameterizedTest(name = "arccos({0}) = {1}")
    @DisplayName("Check table values from CSV")
    @CsvFileSource(resources = "/table_values.csv", numLinesToSkip = 0, delimiter = ';')
    void checkTableValues(double x, double expectedY) {
        assertAll(
                () -> assertEquals(expectedY, Trigonometric.arccos(x), DELTA)
        );
    }

    @Test
    @DisplayName("Fuzzy testing with random values")
    void checkRandomDots() {
        for (int i = 0; i < 1000; i++) {
            // Генерируем значения строго внутри диапазона, избегая самих 1.0 и -1.0 в рандоме
            double randomValue = ThreadLocalRandom.current().nextDouble(-0.999, 0.999);
            assertEquals(Math.acos(randomValue), Trigonometric.arccos(randomValue), DELTA);
        }
    }
}