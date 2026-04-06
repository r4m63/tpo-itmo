package ru.itmo.qa.lab2.trig.module;

import static java.lang.String.format;
import static java.math.BigDecimal.ONE;
import static java.math.RoundingMode.HALF_EVEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.math.MathContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.ValueSource;

import ch.obermuhlner.math.big.BigDecimalMath;
import ru.itmo.qa.lab2.trig.Cosecant;

// Проверяет модульную реализацию косеканса:
// значения в опорных точках, исключения в точках разрыва и табличные результаты.
class CosecantTest {
    private static final BigDecimal PRECISION = new BigDecimal("0.0000001");

    private Cosecant csc;

    @BeforeEach
    // Создает свежий экземпляр функции перед каждым тестом,
    // чтобы все проверки шли на чистом состоянии.
    void init() {
        csc = new Cosecant();
    }

    @Test
    // Проверяет опорную точку csc(pi/2)=1, где функция принимает
    // минимальное по модулю положительное значение на стандартном периоде.
    @DisplayName("Должен вычислять косеканс в точке pi/2")
    void shouldCalculateForMinimum() {
        MathContext mc = new MathContext(PRECISION.scale(), HALF_EVEN);
        BigDecimal arg = BigDecimalMath.pi(mc).divide(BigDecimal.valueOf(2), mc);
        assertEquals(ONE.setScale(PRECISION.scale(), HALF_EVEN), csc.calculate(arg, PRECISION));
    }

    @Test
    // Проверяет симметричную опорную точку csc(-pi/2)=-1, чтобы зафиксировать
    // правильный знак функции на отрицательной полуоси.
    @DisplayName("Должен вычислять косеканс в точке -pi/2")
    void shouldCalculateForMaximum() {
        MathContext mc = new MathContext(PRECISION.scale(), HALF_EVEN);
        BigDecimal arg = BigDecimalMath.pi(mc).divide(BigDecimal.valueOf(2), mc).negate();
        assertEquals(ONE.setScale(PRECISION.scale(), HALF_EVEN).negate(), csc.calculate(arg, PRECISION));
    }

    // Проверяет точки, где sin(x)=0 и потому csc(x) не определен:
    // реализация должна сообщать об ошибке с понятным текстом.
    @DisplayName("Должен отклонять точки, где косеканс не определён")
    @ParameterizedTest
    @ValueSource(doubles = { -Math.PI, 0, Math.PI })
    void shouldNotCalculateForPi(double x) {
        final BigDecimal arg = BigDecimal.valueOf(x).setScale(PRECISION.scale(), HALF_EVEN);
        Throwable exception = assertThrows(ArithmeticException.class, () -> csc.calculate(arg, PRECISION));
        String msg = format("У косеканса нет значения при x = %s", arg);
        assertEquals(msg, exception.getMessage());
    }

    // Сверяет реализацию косеканса с эталонной таблицей значений на наборе точек.
    @DisplayName("Должен вычислять косеканс по табличным значениям")
    @ParameterizedTest(name = "csc({0})")
    @CsvFileSource(resources = "/csc.csv", numLinesToSkip = 1, delimiter = ',')
    void testCsc(BigDecimal x, BigDecimal y) {
        assertEquals(y, csc.calculate(x, PRECISION));
    }
}
