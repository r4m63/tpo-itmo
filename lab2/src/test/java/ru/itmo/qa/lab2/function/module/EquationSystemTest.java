package ru.itmo.qa.lab2.function.module;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.itmo.qa.lab2.DecimalAssertions.assertClose;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import ru.itmo.qa.lab2.EquationSystem;

// тестирует настоящую реализацию системы функций
class EquationSystemTest {

    private static final BigDecimal DEFAULT_PRECISION = new BigDecimal("0.0000001");

    private EquationSystem system;

    @BeforeEach
    void init() {
        system = new EquationSystem();
    }

    @Test
    @DisplayName("Должен отклонять null в качестве аргумента системы")
    void shouldNotAcceptNullArgument() {
        assertThrows(NullPointerException.class, () -> system.calculate(null, DEFAULT_PRECISION));
    }

    @Test
    @DisplayName("Должен отклонять null в качестве точности системы")
    void shouldNotAcceptNullPrecision() {
        BigDecimal arg = new BigDecimal(-2);
        assertThrows(NullPointerException.class, () -> system.calculate(arg, null));
    }

    @DisplayName("Должен отклонять некорректные значения точности")
    @ParameterizedTest
    @MethodSource("illegalPrecisions")
    void shouldNotAcceptIncorrectPrecisions(final BigDecimal precision) {
        BigDecimal arg = new BigDecimal(-2);
        assertThrows(ArithmeticException.class, () -> system.calculate(arg, precision));
    }

    @DisplayName("Должен отклонять точки вне области определения тригонометрической ветки")
    @ParameterizedTest
    @MethodSource("undefinedValuesForNonPositiveBranch")
    void shouldNotAcceptUndefinedValuesForNonPositiveBranch(final BigDecimal x) {
        assertThrows(ArithmeticException.class, () -> system.calculate(x, DEFAULT_PRECISION));
    }

    @DisplayName("Должен отклонять точки асимптот системы")
    @ParameterizedTest
    @ValueSource(doubles = { -Math.PI / 2, -3 * Math.PI / 2, -5 * Math.PI / 2 })
    void shouldNotAcceptAsymptotes(double x) {
        BigDecimal arg = new BigDecimal(x);
        assertThrows(ArithmeticException.class, () -> system.calculate(arg, DEFAULT_PRECISION));
    }

    @Test
    @DisplayName("Должен корректно вычислять систему в нуле")
    void shouldCalculateForZero() {
        assertEquals(new BigDecimal("-1.0000000"), system.calculate(BigDecimal.ZERO, DEFAULT_PRECISION));
    }

    @Test
    @DisplayName("Должен корректно вычислять систему на положительном аргументе")
    void shouldCalculateForPositiveArgument() {
        BigDecimal arg = BigDecimal.valueOf(10);
        assertClose(new BigDecimal("9.9797096"), system.calculate(arg, DEFAULT_PRECISION));
    }

    @DisplayName("Должен вычислять систему по табличным эталонным значениям")
    @ParameterizedTest(name = "f({0}) = {1}")
    @CsvFileSource(resources = "/system.csv", numLinesToSkip = 1, delimiter = ',')
    void testSystem(BigDecimal x, BigDecimal y) {
        assertClose(y, system.calculate(x, DEFAULT_PRECISION));
    }

    private static Stream<Arguments> illegalPrecisions() {
        return Stream.of(
                Arguments.of(BigDecimal.valueOf(1)),
                Arguments.of(BigDecimal.valueOf(0)),
                Arguments.of(BigDecimal.valueOf(1.01)),
                Arguments.of(BigDecimal.valueOf(-0.01)));
    }

    private static Stream<Arguments> undefinedValuesForNonPositiveBranch() {
        return Stream.of(
                Arguments.of(BigDecimal.valueOf(-Math.PI / 2).setScale(10, RoundingMode.HALF_EVEN)),
                Arguments.of(BigDecimal.valueOf(-3 * Math.PI / 2).setScale(10, RoundingMode.HALF_EVEN)));
    }
}
