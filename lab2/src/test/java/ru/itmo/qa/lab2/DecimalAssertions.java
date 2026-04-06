package ru.itmo.qa.lab2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

public final class DecimalAssertions {
    private static final BigDecimal DEFAULT_TOLERANCE = new BigDecimal("0.00001");

    private DecimalAssertions() {
    }

    public static void assertClose(final BigDecimal expected, final BigDecimal actual) {
        assertClose(expected, actual, DEFAULT_TOLERANCE);
    }

    public static void assertClose(final BigDecimal expected, final BigDecimal actual, final BigDecimal tolerance) {
        final BigDecimal delta = expected.subtract(actual).abs();
        assertTrue(
                delta.compareTo(tolerance) <= 0,
                () -> "Expected " + expected + ", actual " + actual + ", delta " + delta + ", tolerance " + tolerance);
    }
}
