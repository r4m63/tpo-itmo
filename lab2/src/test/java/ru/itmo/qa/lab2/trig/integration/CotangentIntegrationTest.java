package ru.itmo.qa.lab2.trig.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

import ru.itmo.qa.lab2.trig.Cosine;
import ru.itmo.qa.lab2.trig.Cotangent;
import ru.itmo.qa.lab2.trig.Sine;

// Проверяет, что cotangent правильно собран из sine и cosine:
// вызывает обе зависимости, корректно делит cos/sin и валидирует особые точки.
@ExtendWith(MockitoExtension.class)
class CotangentIntegrationTest {

    private static final BigDecimal PRECISION = new BigDecimal("0.0000001");

    // Управляемый mock синуса для проверки формулы cot без реального расчета sin(x).
    @Mock
    private Sine mockSin;

    // Реальный синус под наблюдением: нужен для проверки факта вызова зависимости.
    @Spy
    private Sine spySin;

    // Управляемый mock косинуса для проверки формулы cot без реального расчета cos(x).
    @Mock
    private Cosine mockCos;

    // Реальный косинус под наблюдением: нужен для проверки факта вызова зависимости.
    @Spy
    private Cosine spyCos;

    @Test
    // Проверяет, что Cotangent в процессе вычисления обращается сразу к двум
    // нижележащим функциям, а не содержит отдельную независимую реализацию.
    @DisplayName("Test 1: Call both sine and cosine")
    void shouldCallSineAndCosineFunction() {
        Cotangent cot = new Cotangent(spySin, spyCos);
        cot.calculate(new BigDecimal(979), PRECISION);
        verify(spySin, atLeastOnce()).calculate(any(BigDecimal.class), any(BigDecimal.class));
        verify(spyCos, atLeastOnce()).calculate(any(BigDecimal.class), any(BigDecimal.class));
    }

    // Подставляет заранее известные sin(x) и cos(x), чтобы проверить именно
    // интеграционную формулу cot(x)=cos(x)/sin(x) на табличных примерах.
    @ParameterizedTest(name = "mock.cot({0}) = {1}")
    @DisplayName("Test 2: Call cotangent")
    @CsvFileSource(resources = "/integration/cotIT.csv", numLinesToSkip = 1, delimiter = ',')
    void shouldCallCotangentFunction(BigDecimal x, BigDecimal y) {
        when(mockSin.calculate(eq(x), any()))
                .thenReturn(new BigDecimal(Math.sin(x.doubleValue())));
        when(mockCos.calculate(eq(x), any()))
                .thenReturn(new BigDecimal(Math.cos(x.doubleValue())));

        Cotangent cot = new Cotangent(mockSin, mockCos);
        assertEquals(y, cot.calculate(x, PRECISION));
    }

    @Test
    // Фиксирует особый случай cotangent: при sin(x)=0 функция не определена
    // и должна завершаться исключением, а не возвращать некорректное число.
    @DisplayName("Cotangent throws exception when sin(x) = 0")
    void shouldThrowWhenSineIsZero() {
        BigDecimal x = BigDecimal.valueOf(Math.PI); // sin(π) = 0
        when(mockSin.calculate(eq(x), any())).thenReturn(BigDecimal.ZERO);
        when(mockCos.calculate(eq(x), any())).thenReturn(BigDecimal.ONE);

        Cotangent cot = new Cotangent(mockSin, mockCos);
        assertThrows(
                ArithmeticException.class,
                () -> cot.calculate(x, PRECISION),
                "Should throw when sin(x) = 0");
    }
}
