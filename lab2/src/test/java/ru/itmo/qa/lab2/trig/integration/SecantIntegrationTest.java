package ru.itmo.qa.lab2.trig.integration;

import static ru.itmo.qa.lab2.DecimalAssertions.assertClose;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import ru.itmo.qa.lab2.stub.CosineTableStub;
import ru.itmo.qa.lab2.trig.Secant;

// Проверяет интеграцию Secant с модулем Cosine:
// sec(x) должен корректно вычисляться как обратное значение косинуса.
class SecantIntegrationTest {

    private static final BigDecimal PRECISION = new BigDecimal("0.0000001");

    // Подменяет косинус табличной заглушкой и проверяет, что secant
    // правильно использует ее значения для вычисления итогового результата.
    @DisplayName("Должен вычислять секанс через табличную заглушку косинуса")
    @ParameterizedTest(name = "sec({0}) = {1}")
    @CsvFileSource(resources = "/sec.csv", numLinesToSkip = 1, delimiter = ',')
    void shouldCalculateSecantWithCosineStub(BigDecimal x, BigDecimal y) {
        final Secant sec = new Secant(new CosineTableStub());
        assertClose(y, sec.calculate(x, PRECISION));
    }
}
