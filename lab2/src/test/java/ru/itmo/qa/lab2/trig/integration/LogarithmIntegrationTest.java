package ru.itmo.qa.lab2.trig.integration;

import static ru.itmo.qa.lab2.DecimalAssertions.assertClose;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import ru.itmo.qa.lab2.stub.NaturalLogarithmTableStub;
import ru.itmo.qa.lab2.trig.BaseNLogarithm;

// Проверяет, что логарифмы по основанию 2, 3 и 10 корректно строятся
// поверх натурального логарифма и не требуют отдельной табличной логики.
class LogarithmIntegrationTest {

    private static final BigDecimal PRECISION = new BigDecimal("0.0000001");

    // Подменяет натуральный логарифм таблицей и проверяет, что log2
    // правильно вычисляется через отношение ln(x) / ln(2).
    @DisplayName("Должен вычислять log2 через табличную заглушку натурального логарифма")
    @ParameterizedTest(name = "log2({0}) = {1}")
    @CsvFileSource(resources = "/log2.csv", numLinesToSkip = 1, delimiter = ',')
    void shouldCalculateLog2WithLnStub(BigDecimal x, BigDecimal y) {
        final BaseNLogarithm logarithm = new BaseNLogarithm(2, new NaturalLogarithmTableStub());
        assertClose(y, logarithm.calculate(x, PRECISION));
    }

    // Аналогично проверяет формулу перехода к основанию 3 через заглушку ln.
    @DisplayName("Должен вычислять log3 через табличную заглушку натурального логарифма")
    @ParameterizedTest(name = "log3({0}) = {1}")
    @CsvFileSource(resources = "/log3.csv", numLinesToSkip = 1, delimiter = ',')
    void shouldCalculateLog3WithLnStub(BigDecimal x, BigDecimal y) {
        final BaseNLogarithm logarithm = new BaseNLogarithm(3, new NaturalLogarithmTableStub());
        assertClose(y, logarithm.calculate(x, PRECISION));
    }

    // Проверяет тот же механизм для десятичного логарифма, чтобы убедиться,
    // что разные основания работают через одну и ту же зависимость ln.
    @DisplayName("Должен вычислять log10 через табличную заглушку натурального логарифма")
    @ParameterizedTest(name = "log10({0}) = {1}")
    @CsvFileSource(resources = "/log10.csv", numLinesToSkip = 1, delimiter = ',')
    void shouldCalculateLog10WithLnStub(BigDecimal x, BigDecimal y) {
        final BaseNLogarithm logarithm = new BaseNLogarithm(10, new NaturalLogarithmTableStub());
        assertClose(y, logarithm.calculate(x, PRECISION));
    }
}
