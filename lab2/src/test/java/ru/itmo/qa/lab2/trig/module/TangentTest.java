package ru.itmo.qa.lab2.trig.module;

import static java.lang.String.format;
import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_EVEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.ValueSource;

import ru.itmo.qa.lab2.trig.Tangent;

// Проверяет модульную реализацию тангенса:
// нули функции, точки разрыва и совпадение с табличными значениями.
class TangentTest {
    private static final BigDecimal PRECISION = new BigDecimal("0.0000001");
    private Tangent tan;

    @BeforeEach
    // Создает новый экземпляр тангенса перед каждым тестом,
    // чтобы каждая проверка выполнялась независимо.
    void init() {
        tan = new Tangent();
    }

    // Проверяет точки вида k*pi, где tan(x)=0, что удобно для контроля
    // правильности знака и базовой формулы sin(x)/cos(x).
    @DisplayName("Должен вычислять тангенс в точках вида k*pi")
    @ParameterizedTest(name = "tan({0})")
    @ValueSource(doubles = { -Math.PI, 0, Math.PI })
    void shouldCalculateForPi(double x) {
        assertEquals(ZERO.setScale(PRECISION.scale(), HALF_EVEN), tan.calculate(BigDecimal.valueOf(x), PRECISION));
    }

    // Проверяет асимптоты tan(x), где cos(x)=0: функция не определена и
    // должна сообщать об этом через ArithmeticException.
    @DisplayName("Должен отклонять точки, где тангенс не определён")
    @ParameterizedTest
    @ValueSource(doubles = { -Math.PI / 2, Math.PI / 2 })
    void shouldNotCalculateForPiHalf(double x) {
        BigDecimal arg = BigDecimal.valueOf(x).setScale(PRECISION.scale(), HALF_EVEN);
        Throwable exception = assertThrows(ArithmeticException.class, () -> tan.calculate(arg, PRECISION));
        String msg = format("У тангенса нет значения при x = %s", arg);
        assertEquals(msg, exception.getMessage());
    }

    // Сверяет вычисления тангенса с табличными эталонными значениями из CSV.
    @DisplayName("Должен вычислять тангенс по табличным значениям")
    @ParameterizedTest(name = "tan({0})")
    @CsvFileSource(resources = "/tan.csv", numLinesToSkip = 1, delimiter = ',')
    void testTan(BigDecimal x, BigDecimal y) {
        assertEquals(y, tan.calculate(x, PRECISION));
    }
}
