package ru.itmo.qa.lab2.trig;

import ru.itmo.qa.lab2.function.AbstractFunction;

import java.math.BigDecimal;

import static java.lang.String.format;
import static java.math.RoundingMode.HALF_EVEN;

public class Cosecant extends AbstractFunction {

  private final Sine sine;

  public Cosecant() {
    super();
    this.sine = new Sine();
  }

  public Cosecant(Sine sine) {
    super();
    this.sine = sine;
  }

  @Override
  public BigDecimal calculate(BigDecimal x, BigDecimal precision) throws ArithmeticException {
    isValid(x, precision);
    BigDecimal sin = sine.calculate(x, precision.setScale(precision.scale() + 12, HALF_EVEN));

    if (sin.abs().compareTo(precision) < 0) {
      throw new ArithmeticException(format("У косеканса нет значения при x = %s", x));
    }

    return BigDecimal.ONE.divide(sin, precision.scale(), HALF_EVEN);
  }
}
