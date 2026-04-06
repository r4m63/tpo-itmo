package ru.itmo.qa.lab2.function.integration;

import static ru.itmo.qa.lab2.DecimalAssertions.assertClose;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import ru.itmo.qa.lab2.EquationSystem;
import ru.itmo.qa.lab2.stub.CosineTableStub;
import ru.itmo.qa.lab2.stub.Log10TableStub;
import ru.itmo.qa.lab2.stub.Log2TableStub;
import ru.itmo.qa.lab2.stub.Log3TableStub;
import ru.itmo.qa.lab2.stub.NaturalLogarithmTableStub;
import ru.itmo.qa.lab2.stub.SecantTableStub;
import ru.itmo.qa.lab2.stub.SineTableStub;

class EquationSystemIntegrationTest {

    private static final BigDecimal PRECISION = new BigDecimal("0.0000001");

    // проверяет, что сама система функций собрана правильно из модулей
    // Для каждого x из systemIT.csv считается результат системы
    // Он сравнивается с ожидаемым y
    // проверить, что ветвление x <= 0 и x > 0 работает правильно
    // проверить, что формула в EquationSystem.java собрана правильно
    // не смешивать ошибку системы с ошибками нижних функций
    @DisplayName("Должен вычислять систему функций с табличными заглушками")
    @ParameterizedTest(name = "f({0}) = {1}")
    @CsvFileSource(resources = "/integration/systemIT.csv", numLinesToSkip = 1, delimiter = ',')
    void shouldCalculateSystemWithTabularStubs(BigDecimal x, BigDecimal y) {
        final EquationSystem system = new EquationSystem(
                new SineTableStub(),
                new CosineTableStub(),
                new SecantTableStub(),
                new NaturalLogarithmTableStub(),
                new Log2TableStub(),
                new Log3TableStub(),
                new Log10TableStub());
        assertClose(y, system.calculate(x, PRECISION));
    }
}
