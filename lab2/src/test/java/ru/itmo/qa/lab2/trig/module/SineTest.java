package ru.itmo.qa.lab2.trig.module;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.math.MathContext.DECIMAL128;
import static java.math.RoundingMode.HALF_EVEN;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.itmo.qa.lab2.DecimalAssertions.assertClose;

import java.math.BigDecimal;
import java.math.MathContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import ch.obermuhlner.math.big.BigDecimalMath;
import ru.itmo.qa.lab2.trig.Sine;

// Проверяет модульную реализацию синуса:
// опорные точки, знаки в симметричных значениях и табличные эталоны.
class SineTest {
    private static final BigDecimal PRECISION = new BigDecimal("0.0000001");

    private Sine sin;

    @BeforeEach
    // Создает новый экземпляр синуса перед каждым тестом,
    // чтобы все проверки работали с одной и той же конфигурацией.
    void init() {
        sin = new Sine();
    }

    @Test
    // Проверяет базовую точку sin(0)=0, которая должна вычисляться точно
    // и с правильной шкалой результата.
    @DisplayName("Должен вычислять синус в нуле")
    void shouldCalculateForZero() {
        assertEquals(ZERO.setScale(7, HALF_EVEN), sin.calculate(ZERO, PRECISION));
    }

    @Test
    // Проверяет значения sin(pi/2)=1 и sin(-pi/2)=-1, чтобы зафиксировать
    // и опорные экстремумы, и нечетность функции по знаку.
    @DisplayName("Должен вычислять синус в точках pi/2 и -pi/2")
    void shouldCalculateForPiHalf() {
        final MathContext mc = new MathContext(DECIMAL128.getPrecision(), HALF_EVEN);
        final BigDecimal arg = BigDecimalMath.pi(mc).divide(BigDecimal.valueOf(2), DECIMAL128.getPrecision(),
                HALF_EVEN);
        assertAll(
                () -> assertEquals(ONE.setScale(7, HALF_EVEN), sin.calculate(arg, PRECISION)),
                () -> assertEquals(ONE.negate().setScale(7, HALF_EVEN), sin.calculate(arg.negate(), PRECISION)));
    }

    // Сверяет вычисления синуса с эталонной таблицей значений на наборе аргументов.
    @DisplayName("Должен вычислять синус по табличным значениям")
    @ParameterizedTest(name = "sin({0})")
    @CsvFileSource(resources = "/sin.csv", numLinesToSkip = 1, delimiter = ',')
    void testSin(BigDecimal x, BigDecimal y) {
        assertClose(y, sin.calculate(x, PRECISION));
    }
}
