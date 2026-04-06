package ru.itmo.qa.lab2.trig.integration;

import static ru.itmo.qa.lab2.DecimalAssertions.assertClose;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import ru.itmo.qa.lab2.stub.SineShiftedForCosineTableStub;
import ru.itmo.qa.lab2.trig.Cosine;

// Проверяет интеграцию Cosine с его единственной зависимостью:
// косинус должен корректно вычисляться через сдвинутый синус.
class CosineIntegrationTest {
    private static final BigDecimal PRECISION = new BigDecimal("0.0000001");

    // Подменяет внутренний синус табличной заглушкой cos(x)=sin(x+pi/2)
    // и сверяет итоговое значение косинуса с эталонной таблицей.
    @DisplayName("Должен вычислять косинус через табличную заглушку синуса")
    @ParameterizedTest(name = "cos({0}) = {1}")
    @CsvFileSource(resources = "/cos.csv", numLinesToSkip = 1, delimiter = ',')
    void shouldCalculateCosineWithSineStub(BigDecimal x, BigDecimal y) {
        final Cosine cosine = new Cosine(new SineShiftedForCosineTableStub());
        assertClose(y, cosine.calculate(x, PRECISION));
    }
}
