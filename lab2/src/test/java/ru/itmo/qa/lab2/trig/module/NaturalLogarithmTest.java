package ru.itmo.qa.lab2.trig.module;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.itmo.qa.lab2.DecimalAssertions.assertClose;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import ru.itmo.qa.lab2.trig.NaturalLogarithm;

// Проверяет модульную реализацию натурального логарифма:
// область определения, специальную точку x=1 и табличные результаты.
class NaturalLogarithmTest {

    private NaturalLogarithm ln;
    private static final BigDecimal PRECISION = new BigDecimal("0.0000001");

    @BeforeEach
    // Создает новую реализацию ln перед каждым тестом,
    // чтобы все проверки шли в одинаковом исходном состоянии.
    void init() {
        ln = new NaturalLogarithm();
    }

    @Test
    // Проверяет граничный случай ln(0), который находится вне области
    // определения и потому должен завершаться исключением.
    @DisplayName("Должен отклонять ноль для натурального логарифма")
    void shouldNotCalculateForZero() {
        assertThrows(ArithmeticException.class, () -> ln.calculate(ZERO, PRECISION));
    }

    @Test
    // Фиксирует основное тождество ln(1)=0 как обязательную опорную точку
    // для корректности всей реализации логарифма.
    @DisplayName("Должен возвращать ноль для натурального логарифма в точке x = 1")
    void shouldCalculateForOne() {
        assertEquals(ZERO, ln.calculate(ONE, PRECISION));
    }

    // Сверяет численные результаты ln(x) с табличными эталонами на наборе точек.
    @DisplayName("Должен вычислять натуральный логарифм по табличным значениям")
    @ParameterizedTest(name = "ln({0}) = {1}")
    @CsvFileSource(resources = "/ln.csv", numLinesToSkip = 1, delimiter = ',')
    void testLn(BigDecimal x, BigDecimal y) {
        assertClose(y, ln.calculate(x, PRECISION));
    }
}
