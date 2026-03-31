package ru.itmo.qa.lab2;

import lombok.AllArgsConstructor;
import ru.itmo.qa.lab2.function.AbstractFunction;
import ru.itmo.qa.lab2.log.BaseNLogarithm;
import ru.itmo.qa.lab2.log.NaturalLogarithm;
import ru.itmo.qa.lab2.trig.*;

import java.math.BigDecimal;
import java.math.MathContext;

import static java.lang.String.format;
import static java.math.BigDecimal.ZERO;
import static java.math.MathContext.DECIMAL128;
import static java.math.RoundingMode.HALF_EVEN;

@AllArgsConstructor
public class EquationSystem extends AbstractFunction {
  private final Sine sin;
  private final Cosine cos;
  private final Secant sec;
  private final Cosecant csc;
  private final Tangent tan;

  private final NaturalLogarithm ln;
  private final BaseNLogarithm log2;
  private final BaseNLogarithm log3;
  private final BaseNLogarithm lg;

  public EquationSystem() {
    super();
    sin = new Sine();
    cos = new Cosine();
    sec = new Secant();
    csc = new Cosecant();
    tan = new Tangent();

    ln = new NaturalLogarithm();
    log2 = new BaseNLogarithm(2);
    log3 = new BaseNLogarithm(3);
    lg = new BaseNLogarithm(10);
  }

  @Override
  public BigDecimal calculate(BigDecimal x, BigDecimal precision) {
    final MathContext mc = new MathContext(DECIMAL128.getPrecision(), HALF_EVEN);
    final BigDecimal p = precision.setScale(precision.scale() + 10, HALF_EVEN);

    if (x.compareTo(ZERO) <= 0) {
      // x <= 0 : (((((tan(x) - sec(x)) - csc(x)) ^ 2) - tan(x)) * ((sin(x) / tan(x)) - (tan(x) / cos(x))))
      try {
        return (
          (
            ((c(tan, x, p).subtract(c(sec, x, p))).subtract(c(csc, x, p))).pow(2, mc)
          ).subtract(c(tan, x, p))
        ).multiply(
          (
            (
              (c(sin, x, p).divide(c(tan, x, p), mc.getPrecision(), HALF_EVEN))
            ).subtract(
              (c(tan, x, p).divide(c(cos, x, p), mc.getPrecision(), HALF_EVEN))
            )
          ),
          mc
        ).setScale(precision.scale(), HALF_EVEN);
      } catch (ArithmeticException e) {
        throw new ArithmeticException(format("У функции нет значения при x = %s", x));
      }

    } else {
      // x > 0 : (((((log_2(x) ^ 2) / ln(x)) / log_3(x)) ^ 3) * (ln(x) / (log_3(x) / (log_10(x) / log_2(x)))))
      try {
        return (
          (
            (
              (
                (
                  c(log2, x, p).pow(2, mc)
                ).divide(c(ln, x, p), mc.getPrecision(), HALF_EVEN)
              ).divide(c(log3, x, p), mc.getPrecision(), HALF_EVEN)
            ).pow(3, mc)
          ).multiply(
            (
              c(ln, x, p)
                .divide(
                  c(log3, x, p)
                    .divide(
                      c(lg, x, p)
                        .divide(
                          c(log2, x, p),
                          mc.getPrecision(), HALF_EVEN
                        ),
                      mc.getPrecision(), HALF_EVEN
                    ),
                  mc.getPrecision(), HALF_EVEN
                )
            ),
            mc
          )
        ).setScale(precision.scale(), HALF_EVEN);
      } catch (ArithmeticException e) {
        throw new ArithmeticException(format("У функции нет значения при x = %s", x));
      }
    }
  }

  private BigDecimal c(AbstractFunction function, BigDecimal x, BigDecimal precision) {
    return function.calculate(x, precision);
  }
}
