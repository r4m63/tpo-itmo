package ru.itmo.qa.lab2.trig.module;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_EVEN;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.itmo.qa.lab2.DecimalAssertions.assertClose;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import ru.itmo.qa.lab2.trig.BaseNLogarithm;

// Проверяет модульные свойства логарифмов по разным основаниям:
// граничные случаи, специальные точки и совпадение с табличными эталонами.
class BaseNLogarithmTest {

    private static final BigDecimal PRECISION = new BigDecimal("0.0000001");
    private BaseNLogarithm log2;
    private BaseNLogarithm log3;
    private BaseNLogarithm log10;

    @BeforeEach
    // Перед каждым тестом поднимает независимые экземпляры логарифмов,
    // чтобы проверки для разных оснований не влияли друг на друга.
    void init() {
        log2 = new BaseNLogarithm(2);
        log3 = new BaseNLogarithm(3);
        log10 = new BaseNLogarithm(10);
    }

    @Test
    // Проверяет базовое ограничение области определения: log_b(0)
    // не существует, поэтому реализация должна выбрасывать исключение.
    @DisplayName("Должен отклонять ноль для логарифма по основанию 2")
    void shouldNotCalculateForZero() {
        assertThrows(ArithmeticException.class, () -> log2.calculate(ZERO, PRECISION));
    }

    @Test
    // Фиксирует универсальное свойство логарифма: для любого основания
    // допустимого вида значение в точке x = 1 равно нулю.
    @DisplayName("Должен возвращать ноль для логарифмов в точке x = 1")
    void shouldCalculateForOne() {
        assertAll(
                () -> assertEquals(ZERO.setScale(7, HALF_EVEN), log2.calculate(ONE, PRECISION)),
                () -> assertEquals(ZERO.setScale(7, HALF_EVEN), log3.calculate(ONE, PRECISION)),
                () -> assertEquals(ZERO.setScale(7, HALF_EVEN), log10.calculate(ONE, PRECISION)));
    }

    // Проверяет численные результаты log2 на наборе эталонных значений из CSV.
    @DisplayName("Должен вычислять log2 по табличным значениям")
    @ParameterizedTest(name = "log2({0})")
    @CsvFileSource(resources = "/log2.csv", numLinesToSkip = 1, delimiter = ',')
    void testLog2(BigDecimal x, BigDecimal y) {
        assertClose(y, log2.calculate(x, PRECISION));
    }

    // Проверяет численные результаты log3 на наборе эталонных значений из CSV.
    @DisplayName("Должен вычислять log3 по табличным значениям")
    @ParameterizedTest(name = "log3({0})")
    @CsvFileSource(resources = "/log3.csv", numLinesToSkip = 1, delimiter = ',')
    void testLog3(BigDecimal x, BigDecimal y) {
        assertClose(y, log3.calculate(x, PRECISION));
    }

    // Проверяет численные результаты log10 на наборе эталонных значений из CSV.
    @DisplayName("Должен вычислять log10 по табличным значениям")
    @ParameterizedTest(name = "log10({0})")
    @CsvFileSource(resources = "/log10.csv", numLinesToSkip = 1, delimiter = ',')
    void testLog10(BigDecimal x, BigDecimal y) {
        assertClose(y, log10.calculate(x, PRECISION));
    }
}
