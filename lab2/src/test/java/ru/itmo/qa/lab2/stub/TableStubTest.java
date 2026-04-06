package ru.itmo.qa.lab2.stub;

import static ru.itmo.qa.lab2.DecimalAssertions.assertClose;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// Проверяет сами табличные заглушки независимо от формул:
// читаются ли CSV-файлы, и связаны ли конкретные stub-классы
// с нужными наборами эталонных значений.
class TableStubTest {
    private static final BigDecimal PRECISION = new BigDecimal("0.0000001");

    @Test
    // Проверяет заглушки основных модулей системы: тригонометрических,
    // логарифмических и всей системы целиком.
    @DisplayName("Должен возвращать значения из табличных заглушек основных модулей")
    void shouldReturnValuesFromAllPrimaryModuleStubs() {
        assertClose(new BigDecimal("-0.8414710"), new SineTableStub().calculate(new BigDecimal("-1.0"), PRECISION));
        assertClose(new BigDecimal("0.5403023"), new CosineTableStub().calculate(new BigDecimal("-1.0"), PRECISION));
        assertClose(new BigDecimal("1.8508158"), new SecantTableStub().calculate(new BigDecimal("-1.0"), PRECISION));
        assertClose(new BigDecimal("-1.6094379"),
                new NaturalLogarithmTableStub().calculate(new BigDecimal("0.2"), PRECISION));
        assertClose(new BigDecimal("-2.3219281"), new Log2TableStub().calculate(new BigDecimal("0.2"), PRECISION));
        assertClose(new BigDecimal("-1.4649735"), new Log3TableStub().calculate(new BigDecimal("0.2"), PRECISION));
        assertClose(new BigDecimal("-0.6989700"), new Log10TableStub().calculate(new BigDecimal("0.2"), PRECISION));
        assertClose(new BigDecimal("-3.5424214"),
                new EquationSystemTableStub().calculate(new BigDecimal("-1.0"), PRECISION));
    }

    @Test
    // Проверяет вспомогательные заглушки, которые используются в составных
    // формулах, чтобы убедиться в корректном подключении их CSV-таблиц.
    @DisplayName("Должен возвращать значения из табличных заглушек вспомогательных модулей")
    void shouldReturnValuesFromAuxiliaryModuleStubs() {
        assertClose(new BigDecimal("-1.0997502"), new CosecantTableStub().calculate(new BigDecimal("-2"), PRECISION));
        assertClose(new BigDecimal("-0.4576576"), new CotangentTableStub().calculate(new BigDecimal("2"), PRECISION));
        assertClose(new BigDecimal("0.8422884"), new TangentTableStub().calculate(new BigDecimal("0.7"), PRECISION));
    }
}
