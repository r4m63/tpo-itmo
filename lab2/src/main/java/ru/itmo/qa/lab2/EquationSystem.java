package ru.itmo.qa.lab2;

import static java.lang.String.format;
import static java.math.BigDecimal.ZERO;
import static java.math.MathContext.DECIMAL128;
import static java.math.RoundingMode.HALF_EVEN;

import java.math.BigDecimal;
import java.math.MathContext;

import ru.itmo.qa.lab2.function.AbstractFunction;
import ru.itmo.qa.lab2.trig.BaseNLogarithm;
import ru.itmo.qa.lab2.trig.Cosine;
import ru.itmo.qa.lab2.trig.NaturalLogarithm;
import ru.itmo.qa.lab2.trig.Secant;
import ru.itmo.qa.lab2.trig.Sine;

public class EquationSystem extends AbstractFunction {
    private final AbstractFunction sin;
    private final AbstractFunction cos;
    private final AbstractFunction sec;
    private final AbstractFunction ln;
    private final AbstractFunction log2;
    private final AbstractFunction log3;
    private final AbstractFunction lg;

    public EquationSystem(
            final AbstractFunction sin,
            final AbstractFunction cos,
            final AbstractFunction sec,
            final AbstractFunction ln,
            final AbstractFunction log2,
            final AbstractFunction log3,
            final AbstractFunction lg) {
        this.sin = sin;
        this.cos = cos;
        this.sec = sec;
        this.ln = ln;
        this.log2 = log2;
        this.log3 = log3;
        this.lg = lg;
    }

    public EquationSystem() {
        sin = new Sine();
        cos = new Cosine();
        sec = new Secant();
        ln = new NaturalLogarithm();
        log2 = new BaseNLogarithm(2);
        log3 = new BaseNLogarithm(3);
        lg = new BaseNLogarithm(10);
    }

    @Override
    public BigDecimal calculate(BigDecimal x, BigDecimal precision) {
        isValid(x, precision);

        final MathContext mc = new MathContext(DECIMAL128.getPrecision(), HALF_EVEN);
        final BigDecimal p = precision.setScale(precision.scale() + 10, HALF_EVEN);

        if (x.compareTo(ZERO) <= 0) {
            // x <= 0 : ((((cos(x)^2)^2) + sin(x)) / cos(x)) - (sec(x) + (cos(x)^2))
            try {
                final BigDecimal cosValue = c(cos, x, p);
                final BigDecimal sinValue = c(sin, x, p);
                final BigDecimal secValue = c(sec, x, p);
                final BigDecimal cosSquared = cosValue.pow(2, mc);
                final BigDecimal cosFourth = cosSquared.pow(2, mc);

                return cosFourth
                        .add(sinValue, mc)
                        .divide(cosValue, mc.getPrecision(), HALF_EVEN)
                        .subtract(secValue.add(cosSquared, mc), mc)
                        .setScale(precision.scale(), HALF_EVEN);
            } catch (ArithmeticException e) {
                throw new ArithmeticException(format("У функции нет значения при x = %s", x));
            }
        } else {
            // x > 0 : ((((log_10(x)^3) * log_2(x)) * log_2(x)) - ((log_3(x) + ln(x)) +
            // ln(x))) * ln(x)
            try {
                final BigDecimal lnValue = c(ln, x, p);
                final BigDecimal log2Value = c(log2, x, p);
                final BigDecimal log3Value = c(log3, x, p);
                final BigDecimal log10Value = c(lg, x, p);

                final BigDecimal logarithmicProduct = log10Value.pow(3, mc)
                        .multiply(log2Value, mc)
                        .multiply(log2Value, mc);
                final BigDecimal logarithmicSum = log3Value
                        .add(lnValue, mc)
                        .add(lnValue, mc);

                return logarithmicProduct
                        .subtract(logarithmicSum, mc)
                        .multiply(lnValue, mc)
                        .setScale(precision.scale(), HALF_EVEN);
            } catch (ArithmeticException e) {
                throw new ArithmeticException(format("У функции нет значения при x = %s", x));
            }
        }
    }

    private BigDecimal c(AbstractFunction function, BigDecimal x, BigDecimal precision) {
        return function.calculate(x, precision);
    }
}
