package ru.itmo.qa.lab2.trig.module;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.math.MathContext.DECIMAL128;
import static java.math.RoundingMode.HALF_EVEN;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.itmo.qa.lab2.DecimalAssertions.assertClose;

import java.math.BigDecimal;
import java.math.MathContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import ch.obermuhlner.math.big.BigDecimalMath;
import ru.itmo.qa.lab2.trig.Secant;

// Проверяет модульную реализацию секанса:
// значения в опорных точках, разрывы в pi/2 + k*pi и табличные эталоны.
class SecantTest {

    private static final BigDecimal PRECISION = new BigDecimal("0.0000001");

    private Secant sec;

    @BeforeEach
    // Создает новый экземпляр секанса перед каждым тестом,
    // чтобы проверки не зависели от предыдущих вызовов.
    void init() {
        sec = new Secant();
    }

    @Test
    // Проверяет базовую точку sec(0)=1, которая быстро показывает,
    // что функция корректно вычисляет обратное значение косинуса в нуле.
    @DisplayName("Должен вычислять секанс в нуле")
    void shouldCalculateForMinimum() {
        assertEquals(ONE.setScale(7, HALF_EVEN), sec.calculate(ZERO, PRECISION));
    }

    @Test
    // Проверяет точки pi и -pi, где cos(x)=-1, а значит sec(x) тоже должен
    // равняться -1 с корректным знаком и округлением.
    @DisplayName("Должен вычислять секанс в точках pi и -pi")
    void shouldCalculateForMaximum() {
        MathContext mc = new MathContext(7, HALF_EVEN);
        assertAll(
                () -> assertEquals(ONE.negate().setScale(7, HALF_EVEN),
                        sec.calculate(BigDecimalMath.pi(mc).negate(), PRECISION)),
                () -> assertEquals(ONE.negate().setScale(7, HALF_EVEN),
                        sec.calculate(BigDecimalMath.pi(mc), PRECISION)));
    }

    @Test
    // Проверяет точки разрыва sec(x), где cos(x)=0, поэтому обратное значение
    // не существует и должно приводить к осмысленному исключению.
    @DisplayName("Должен отклонять точки, где секанс не определён")
    void shouldNotCalculateForPiHalf() {
        MathContext mc = new MathContext(DECIMAL128.getPrecision(), HALF_EVEN);
        BigDecimal arg = BigDecimalMath.pi(mc).divide(BigDecimal.valueOf(2), mc);
        BigDecimal negativeArg = arg.negate();
        assertAll(
                () -> {
                    Throwable exception = assertThrows(ArithmeticException.class,
                            () -> sec.calculate(negativeArg, PRECISION));
                    String msg = String.format("У секанса нет значения при x = %s", negativeArg);
                    assertEquals(msg, exception.getMessage());
                },
                () -> {
                    Throwable exception = assertThrows(ArithmeticException.class, () -> sec.calculate(arg, PRECISION));
                    String msg = String.format("У секанса нет значения при x = %s", arg);
                    assertEquals(msg, exception.getMessage());
                });
    }

    // Сверяет вычисления секанса с эталонными значениями из CSV-таблицы.
    @DisplayName("Должен вычислять секанс по табличным значениям")
    @ParameterizedTest(name = "sec({0})")
    @CsvFileSource(resources = "/sec.csv", numLinesToSkip = 1, delimiter = ',')
    void testSec(BigDecimal x, BigDecimal y) {
        assertClose(y, sec.calculate(x, PRECISION));
    }
}
