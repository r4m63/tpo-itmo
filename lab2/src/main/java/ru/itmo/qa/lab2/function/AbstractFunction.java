package ru.itmo.qa.lab2.function;

import java.math.BigDecimal;
import java.util.Objects;

public abstract class AbstractFunction {
  public String getName() {
    return getClass().getSimpleName();
  }

  public abstract BigDecimal calculate(final BigDecimal x, final BigDecimal precision);

  protected void isValid(final BigDecimal x, final BigDecimal precision) {
    Objects.requireNonNull(x, "Аргумент не должен быть null");
    Objects.requireNonNull(precision, "Точность не должна быть null");
    if (precision.compareTo(BigDecimal.ZERO) <= 0 || precision.compareTo(BigDecimal.ONE) >= 0) {
      throw new ArithmeticException("Значение точности должно быть между 0 и 1 включительно");
    }
  }
}
