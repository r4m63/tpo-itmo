package ru.itmo.qa.lab2.trig;

import ru.itmo.qa.lab2.function.AbstractFunction;

import java.math.BigDecimal;

import static java.lang.String.format;
import static java.math.RoundingMode.HALF_EVEN;

public class Cotangent extends AbstractFunction {

  private final AbstractFunction sine;
  private final AbstractFunction cosine;

  public Cotangent() {
    this.sine = new Sine();
    this.cosine = new Cosine();
  }

  public Cotangent(AbstractFunction sine, AbstractFunction cosine) {
    this.sine = sine;
    this.cosine = cosine;
  }

  @Override
  public BigDecimal calculate(BigDecimal x, BigDecimal precision) throws ArithmeticException {
    isValid(x, precision);
    BigDecimal sin = sine.calculate(x, precision.setScale(precision.scale() + 5, HALF_EVEN));
    BigDecimal cos = cosine.calculate(x, precision.setScale(precision.scale() + 5, HALF_EVEN));

    if (sin.abs().compareTo(precision) < 0) {
      throw new ArithmeticException(format("У котангенса нет значения при x = %s", x));
    }

    return cos.divide(sin, precision.scale(), HALF_EVEN);
  }
}
