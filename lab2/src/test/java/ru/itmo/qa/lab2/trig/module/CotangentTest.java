package ru.itmo.qa.lab2.trig.module;

import static java.lang.String.format;
import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_EVEN;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.ValueSource;

import ru.itmo.qa.lab2.trig.Cotangent;

// Проверяет модульную реализацию котангенса:
// нули функции, точки вне области определения и табличные значения.
class CotangentTest {
    private static final BigDecimal PRECISION = new BigDecimal("0.0000001");
    private Cotangent cot;

    @BeforeEach
    // Создает новый экземпляр котангенса перед каждым тестом,
    // чтобы проверки не зависели от предыдущих вычислений.
    void init() {
        cot = new Cotangent();
    }

    // Проверяет точки вида pi/2 + k*pi, где cot(x)=0, то есть числитель cos(x)
    // обращается в ноль, а знаменатель sin(x) остается ненулевым.
    @DisplayName("Должен вычислять котангенс в точках вида pi/2 + k*pi")
    @ParameterizedTest(name = "cot({0})")
    @ValueSource(doubles = { -1.5 * Math.PI, -Math.PI / 2, Math.PI / 2, 1.5 * Math.PI })
    void shouldCalculateForPiHalf(double x) {
        BigDecimal arg = BigDecimal.valueOf(x);
        assertEquals(ZERO.setScale(PRECISION.scale(), HALF_EVEN), cot.calculate(arg, PRECISION));
    }

    // Проверяет точки, где sin(x)=0 и потому cot(x) не существует:
    // функция должна выбрасывать ArithmeticException с ожидаемым текстом.
    @DisplayName("Должен отклонять точки, где котангенс не определён")
    @ParameterizedTest
    @ValueSource(doubles = { -Math.PI, 0, Math.PI })
    void shouldNotCalculateForPi(double x) {
        BigDecimal arg = BigDecimal.valueOf(x).setScale(PRECISION.scale(), HALF_EVEN);
        assertAll(
                () -> {
                    Throwable exception = assertThrows(ArithmeticException.class, () -> cot.calculate(arg, PRECISION));
                    String msg = format("У котангенса нет значения при x = %s", arg);
                    assertEquals(msg, exception.getMessage());
                });
    }

    // Сверяет модульную реализацию котангенса с набором эталонных значений из CSV.
    @DisplayName("Должен вычислять котангенс по табличным значениям")
    @ParameterizedTest(name = "cot({0})")
    @CsvFileSource(resources = "/cot.csv", numLinesToSkip = 1, delimiter = ',')
    void testCot(BigDecimal x, BigDecimal y) {
        assertEquals(y, cot.calculate(x, PRECISION));
    }
}
