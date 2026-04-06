package ru.itmo.qa.lab2.function;

import static java.math.BigDecimal.ONE;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import ru.itmo.qa.lab2.EquationSystem;
import ru.itmo.qa.lab2.trig.BaseNLogarithm;
import ru.itmo.qa.lab2.trig.Cosecant;
import ru.itmo.qa.lab2.trig.Cosine;
import ru.itmo.qa.lab2.trig.Cotangent;
import ru.itmo.qa.lab2.trig.NaturalLogarithm;
import ru.itmo.qa.lab2.trig.Secant;
import ru.itmo.qa.lab2.trig.Sine;
import ru.itmo.qa.lab2.trig.Tangent;

// Фиксирует общий контракт для всех реализаций AbstractFunction:
// одинаковую валидацию аргументов и базовую возможность вычисления.
class AbstractFunctionTest {

    private static final BigDecimal PRECISION = new BigDecimal("0.000001");
    private static final BigDecimal NEGATIVE_PRECISION = PRECISION.negate();
    private static final BigDecimal POSITIVE_PRECISION = PRECISION.add(ONE);

    // Проверяет, что любая функция отклоняет null вместо входного аргумента
    // и возвращает ожидаемое сообщение базовой валидации.
    @DisplayName("Должен отклонять null в качестве аргумента функции")
    @ParameterizedTest
    @MethodSource("functions")
    void shouldNotAcceptNullArg(final AbstractFunction function) {
        Throwable exception = assertThrows(NullPointerException.class, () -> function.calculate(null, PRECISION));
        assertEquals("Аргумент не должен быть null", exception.getMessage());
    }

    // Проверяет, что общая проверка входных данных запрещает null-точность
    // для любой конкретной реализации функции.
    @DisplayName("Должен отклонять null в качестве точности")
    @ParameterizedTest
    @MethodSource("functions")
    void shouldNotAcceptNullPrecision(final AbstractFunction function) {
        Throwable exception = assertThrows(NullPointerException.class, () -> function.calculate(ONE, null));
        assertEquals("Точность не должна быть null", exception.getMessage());
    }

    // Проверяет обе границы некорректной точности: отрицательное значение
    // и число больше допустимого интервала (0, 1).
    @DisplayName("Должен отклонять точность вне интервала от 0 до 1")
    @ParameterizedTest
    @MethodSource("functions")
    void shouldNotAcceptOutside0And1(final AbstractFunction function) {
        assertAll(
                () -> {
                    Throwable exception = assertThrows(ArithmeticException.class,
                            () -> function.calculate(ONE, NEGATIVE_PRECISION));
                    assertEquals("Значение точности должно быть между 0 и 1 включительно", exception.getMessage());
                },
                () -> {
                    Throwable exception = assertThrows(ArithmeticException.class,
                            () -> function.calculate(ONE, POSITIVE_PRECISION));
                    assertEquals("Значение точности должно быть между 0 и 1 включительно", exception.getMessage());
                });
    }

    // Подтверждает, что при валидных аргументе и точности базовый контракт
    // не выбрасывает исключение вне зависимости от конкретной функции.
    @DisplayName("Должен принимать корректные аргумент и точность")
    @ParameterizedTest
    @MethodSource("functions")
    void shouldAcceptArgAndPrecision(final AbstractFunction function) {
        assertDoesNotThrow(() -> function.calculate(ONE, PRECISION));
    }

    // Собирает все реализации, которые должны проходить единый набор
    // базовых контрактных тестов из этого класса.
    private static Stream<Arguments> functions() {
        return Stream.of(
                Arguments.of(new Sine()),
                Arguments.of(new Cosine()),
                Arguments.of(new Secant()),
                Arguments.of(new Cosecant()),
                Arguments.of(new Tangent()),
                Arguments.of(new Cotangent()),
                Arguments.of(new NaturalLogarithm()),
                Arguments.of(new BaseNLogarithm(2)),
                Arguments.of(new BaseNLogarithm(3)),
                Arguments.of(new BaseNLogarithm(5)),
                Arguments.of(new BaseNLogarithm(10)),
                Arguments.of(new EquationSystem()));
    }
}
