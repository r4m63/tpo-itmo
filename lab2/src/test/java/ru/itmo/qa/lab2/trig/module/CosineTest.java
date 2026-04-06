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
import ru.itmo.qa.lab2.trig.Cosine;
import ru.itmo.qa.lab2.trig.Sine;

// Проверяет модульную реализацию косинуса:
// опорные точки, характерные нули и совпадение с эталонной таблицей.
class CosineTest {
    private static final BigDecimal PRECISION = new BigDecimal("0.0000001");

    private Cosine cos;

    @BeforeEach
    // Поднимает косинус с реальной зависимостью Sine, чтобы тестировать
    // модуль в обычной конфигурации без заглушек.
    void init() {
        cos = new Cosine(new Sine());
    }

    @Test
    // Проверяет базовую точку cos(0)=1, удобную для быстрой валидации
    // знака, масштаба и корректного округления результата.
    @DisplayName("Должен вычислять косинус в нуле")
    void shouldCalculateForZero() {
        assertEquals(ONE.setScale(7, HALF_EVEN), cos.calculate(ZERO, PRECISION));
    }

    @Test
    // Проверяет семейство точек pi/2 + k*pi, в которых косинус должен
    // обращаться в ноль независимо от знака и смещения на период.
    @DisplayName("Должен вычислять косинус в точках вида pi/2 + k*pi")
    void shouldCalculateForPiHalf() {
        final MathContext mc = new MathContext(DECIMAL128.getPrecision());
        final BigDecimal arg = BigDecimalMath.pi(mc).divide(BigDecimal.valueOf(2), DECIMAL128.getPrecision(),
                HALF_EVEN);
        final BigDecimal expected = ZERO.setScale(7, HALF_EVEN);
        assertAll(
                () -> assertEquals(expected, cos.calculate(arg, PRECISION)),
                () -> assertEquals(expected, cos.calculate(arg.negate(), PRECISION)),
                () -> assertEquals(expected, cos.calculate(arg.multiply(BigDecimal.valueOf(3)), PRECISION)),
                () -> assertEquals(expected, cos.calculate(arg.multiply(BigDecimal.valueOf(3)).negate(), PRECISION)));
    }

    @Test
    // Фиксирует значение cos(±pi)=-1, чтобы проверить поведение функции
    // в характерных точках полного полуоборота.
    @DisplayName("Должен вычислять косинус в точках pi и -pi")
    void shouldCalculateForPi() {
        final MathContext mc = new MathContext(DECIMAL128.getPrecision());
        final BigDecimal arg = BigDecimalMath.pi(mc);
        final BigDecimal expected = ONE.setScale(7, HALF_EVEN).negate();
        assertAll(
                () -> assertEquals(expected, cos.calculate(arg, PRECISION)),
                () -> assertEquals(expected, cos.calculate(arg.negate(), PRECISION)));
    }

    // Сверяет реализацию косинуса с табличными эталонами на наборе значений x.
    @DisplayName("Должен вычислять косинус по табличным значениям")
    @ParameterizedTest(name = "cos({0})")
    @CsvFileSource(resources = "/cos.csv", numLinesToSkip = 1, delimiter = ',')
    void testCos(BigDecimal x, BigDecimal y) {
        assertClose(y, cos.calculate(x, PRECISION));
    }
}
