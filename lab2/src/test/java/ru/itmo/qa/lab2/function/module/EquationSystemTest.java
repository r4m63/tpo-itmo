package ru.itmo.qa.lab2.function.module;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.itmo.qa.lab2.EquationSystem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Stream;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EquationSystemTest {

  private static final BigDecimal DEFAULT_PRECISION = new BigDecimal("0.0000001");

  private EquationSystem system;

  @BeforeEach
  void init() {
    system = new EquationSystem();
  }

  @Test
  void shouldNotAcceptNullArgument() {
    assertThrows(NullPointerException.class, () -> system.calculate(null, DEFAULT_PRECISION));
  }

  @Test
  void shouldNotAcceptNullPrecision() {
    BigDecimal arg = new BigDecimal(-2);
    assertThrows(NullPointerException.class, () -> system.calculate(arg, null));
  }

  @ParameterizedTest
  @MethodSource("illegalPrecisions")
  void shouldNotAcceptIncorrectPrecisions(final BigDecimal precision) {
    BigDecimal arg = new BigDecimal(-2);
    assertThrows(ArithmeticException.class, () -> system.calculate(arg, precision));
  }

  @ParameterizedTest
  @MethodSource("illegalValuesLt0")
  void shouldNotAcceptIncorrectValuesForLt0(final BigDecimal precision) {
    BigDecimal arg = new BigDecimal(-2);
    assertThrows(ArithmeticException.class, () -> system.calculate(arg, precision));
  }

  @ParameterizedTest
  @ValueSource(doubles = {-Math.PI, 2 * -Math.PI, 3 * -Math.PI})
  void shouldNotAcceptAsymptotes(double x) {
    BigDecimal arg = new BigDecimal(x);
    assertThrows(ArithmeticException.class, () -> system.calculate(arg, DEFAULT_PRECISION));
  }

  @Test
  void shouldCalculateForGt0() {
    BigDecimal arg = BigDecimal.valueOf(1000);
    assertEquals(BigDecimal.valueOf(3.9539834), system.calculate(arg, DEFAULT_PRECISION));
  }

  @Test
  void shouldNotAccept1() {
    Throwable exception = assertThrows(ArithmeticException.class, () -> system.calculate(BigDecimal.ONE, DEFAULT_PRECISION));
    String msg = format("У функции нет значения при x = %s", BigDecimal.ONE);
    assertEquals(msg, exception.getMessage());
  }

  // FIXME
  @ParameterizedTest(name = "f({0}) = {1}")
  @CsvFileSource(resources = "/system.csv", numLinesToSkip = 1, delimiter = ',')
  void testSystem(BigDecimal x, BigDecimal y) {
    assertEquals(y, system.calculate(x, DEFAULT_PRECISION));
  }

  private static Stream<Arguments> illegalPrecisions() {
    return Stream.of(
      Arguments.of(BigDecimal.valueOf(1)),
      Arguments.of(BigDecimal.valueOf(0)),
      Arguments.of(BigDecimal.valueOf(1.01)),
      Arguments.of(BigDecimal.valueOf(-0.01))
    );
  }

  private static Stream<Arguments> illegalValuesLt0() {
    return Stream.of(
      Arguments.of(BigDecimal.valueOf(0)),
      Arguments.of(BigDecimal.valueOf(-Math.PI).divide(BigDecimal.valueOf(2), 10, RoundingMode.HALF_EVEN)),
      Arguments.of(BigDecimal.valueOf(-Math.PI)),
      Arguments.of(BigDecimal.valueOf(-Math.PI).multiply(BigDecimal.valueOf(3)).divide(BigDecimal.valueOf(2), 10, RoundingMode.HALF_EVEN)),
      Arguments.of(BigDecimal.valueOf(-Math.PI).multiply(BigDecimal.valueOf(2)))
    );
  }
}
