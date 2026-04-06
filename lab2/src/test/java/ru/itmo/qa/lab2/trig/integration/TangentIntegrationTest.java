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
import ru.itmo.qa.lab2.trig.Sine;
import ru.itmo.qa.lab2.trig.Tangent;

// Проверяет, что tangent правильно собран из sine и cosine:
// вызывает обе зависимости, корректно делит sin/cos и обрабатывает асимптоты.
@ExtendWith(MockitoExtension.class)
class TangentIntegrationTest {
    private static final BigDecimal PRECISION = new BigDecimal("0.0000001");

    // Управляемый mock синуса для проверки формулы tan без реального расчета sin(x).
    @Mock
    private Sine mockSin;

    // Реальный синус под наблюдением: нужен для проверки обращения к зависимости.
    @Spy
    private Sine spySin;

    // Управляемый mock косинуса для проверки формулы tan без реального расчета cos(x).
    @Mock
    private Cosine mockCos;

    // Реальный косинус под наблюдением: нужен для проверки обращения к зависимости.
    @Spy
    private Cosine spyCos;

    @Test
    // Проверяет, что Tangent не вычисляется "в обход", а действительно
    // использует обе нижележащие функции в процессе расчета.
    @DisplayName("Test 1: Call both sine and cosine")
    void shouldCallSineAndCosineFunction() {
        Tangent tan = new Tangent(spySin, spyCos);
        tan.calculate(new BigDecimal(972), PRECISION);
        verify(spySin, atLeastOnce()).calculate(any(BigDecimal.class), any(BigDecimal.class));
        verify(spyCos, atLeastOnce()).calculate(any(BigDecimal.class), any(BigDecimal.class));
    }

    // Подставляет контролируемые значения sin(x) и cos(x), чтобы проверить
    // саму интеграционную формулу tan(x)=sin(x)/cos(x) на эталонных точках.
    @ParameterizedTest(name = "mock.tan({0}) = {1}")
    @DisplayName("Test 2: Call tangent")
    @CsvFileSource(resources = "/integration/tanIT.csv", numLinesToSkip = 1, delimiter = ',')
    void shouldCallTangentFunction(BigDecimal x, BigDecimal y) {
        when(mockSin.calculate(eq(x), any()))
                .thenReturn(new BigDecimal(Math.sin(x.doubleValue())));
        when(mockCos.calculate(eq(x), any()))
                .thenReturn(new BigDecimal(Math.cos(x.doubleValue())));

        Tangent tan = new Tangent(mockSin, mockCos);
        assertEquals(y, tan.calculate(x, PRECISION));
    }

    @Test
    // Фиксирует асимптоту tangent: при cos(x)=0 функция не определена
    // и должна выбрасывать исключение вместо численного результата.
    @DisplayName("Tangent throws exception when cos(x) = 0")
    void shouldThrowWhenSineIsZero() {
        BigDecimal x = BigDecimal.valueOf(Math.PI).divide(BigDecimal.TWO); // cos(π/2) = 0
        when(mockSin.calculate(eq(x), any())).thenReturn(BigDecimal.ONE);
        when(mockCos.calculate(eq(x), any())).thenReturn(BigDecimal.ZERO);

        Tangent tan = new Tangent(mockSin, mockCos);
        assertThrows(
                ArithmeticException.class,
                () -> tan.calculate(x, PRECISION),
                "Should throw when cos(x) = 0");
    }
}
