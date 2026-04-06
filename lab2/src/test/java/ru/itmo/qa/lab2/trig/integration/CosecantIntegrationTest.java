package ru.itmo.qa.lab2.trig.integration;

import static java.math.RoundingMode.HALF_EVEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.itmo.qa.lab2.trig.Cosecant;
import ru.itmo.qa.lab2.trig.Sine;

// Проверяет, что cosecant корректно интегрируется с зависимостью Sine:
// вызывает ее при вычислении и правильно собирает итоговый результат.
@ExtendWith(MockitoExtension.class) // перед запуском теста создай и инициализируй @Mock и @Spy
class CosecantIntegrationTest {

    private static final BigDecimal PRECISION = new BigDecimal("0.0000001");

    // Полностью поддельный Sine, через который можно изолированно проверить
    // формулу cosecant, не завися от реальной реализации синуса.
    @Mock
    private Sine mockSin;

    // Реальный Sine под наблюдением Mockito: нужен, чтобы убедиться,
    // что Cosecant действительно делегирует вычисление синусу.
    @Spy
    private Sine spySin;

    @Test
    // Проверяет сам факт интеграции: при расчете cosecant должен хотя бы
    // один раз обратиться к вложенной функции синуса.
    @DisplayName("Test 1: Call sine")
    void shouldCallSineFunction() {
        Cosecant csc = new Cosecant(spySin);
        csc.calculate(new BigDecimal(965), PRECISION);
        verify(spySin, atLeastOnce()).calculate(any(BigDecimal.class), any(BigDecimal.class));
    }

    // Подменяет синус контролируемыми значениями и проверяет, что Cosecant
    // по ним выдает табличный результат из integration-набора.
    @DisplayName("Test 2: Call cosecant")
    @ParameterizedTest(name = "mock.csc({0}) = {1}")
    @CsvFileSource(resources = "/integration/cscIT.csv", numLinesToSkip = 1, delimiter = ',')
    void shouldCallCosecantFunction(BigDecimal x, BigDecimal y) {
        when(mockSin.calculate(x, PRECISION.setScale(PRECISION.scale() + 12, HALF_EVEN)))
                .thenReturn(BigDecimal.valueOf(Math.sin(x.doubleValue())));
        Cosecant csc = new Cosecant(mockSin);
        assertEquals(y, csc.calculate(x, PRECISION));
    }
}
