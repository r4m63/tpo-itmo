package ru.itmo.qa.lab2.function.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itmo.qa.lab2.EquationSystem;
import ru.itmo.qa.lab2.log.BaseNLogarithm;
import ru.itmo.qa.lab2.log.NaturalLogarithm;
import ru.itmo.qa.lab2.trig.*;

import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EquationSystemIntegrationTest {

  private static final BigDecimal PRECISION = new BigDecimal("0.0000001");

  @Spy
  private Sine spySin;
  @Spy
  private Cosine spyCos;
  @Spy
  private Secant spySec;
  @Spy
  private Cosecant spyCsc;
  @Spy
  private Tangent spyTan;
  @Spy
  private NaturalLogarithm spyLn;
  @Spy
  private BaseNLogarithm spyLog2;
  @Spy
  private BaseNLogarithm spyLog3;
  @Spy
  private BaseNLogarithm spyLg;

  @Mock
  private Sine mockSin;
  @Mock
  private Cosine mockCos;
  @Mock
  private Secant mockSec;
  @Mock
  private Cosecant mockCsc;
  @Mock
  private Tangent mockTan;
  @Mock
  private NaturalLogarithm mockLn;
  @Mock
  private BaseNLogarithm mockLog2;
  @Mock
  private BaseNLogarithm mockLog3;
  @Mock
  private BaseNLogarithm mockLg;

  @Test
  void shouldCallAllTrigFunctions() {
    EquationSystem system = new EquationSystem(spySin, spyCos, spySec, spyCsc, spyTan, spyLn, spyLog2, spyLog3, spyLg);
    system.calculate(new BigDecimal(-5), new BigDecimal("0.0001"));
    verify(spySin, atLeastOnce()).calculate(any(BigDecimal.class), any(BigDecimal.class));
    verify(spyCos, atLeastOnce()).calculate(any(BigDecimal.class), any(BigDecimal.class));
    verify(spySec, atLeastOnce()).calculate(any(BigDecimal.class), any(BigDecimal.class));
    verify(spyCsc, atLeastOnce()).calculate(any(BigDecimal.class), any(BigDecimal.class));
    verify(spyTan, atLeastOnce()).calculate(any(BigDecimal.class), any(BigDecimal.class));
    verifyNoInteractions(spyLn);
    verifyNoInteractions(spyLog2);
    verifyNoInteractions(spyLog3);
    verifyNoInteractions(spyLg);
  }

  @Test
  void shouldCallAllLogFunctions() {
    EquationSystem system = new EquationSystem(spySin, spyCos, spySec, spyCsc, spyTan, spyLn, spyLog2, spyLog3, spyLg);
    system.calculate(new BigDecimal(5), new BigDecimal("0.0001"));
    verify(spyLn, atLeastOnce()).calculate(any(BigDecimal.class), any(BigDecimal.class));
    verify(spyLog2, atLeastOnce()).calculate(any(BigDecimal.class), any(BigDecimal.class));
    verify(spyLog3, atLeastOnce()).calculate(any(BigDecimal.class), any(BigDecimal.class));
    verify(spyLg, atLeastOnce()).calculate(any(BigDecimal.class), any(BigDecimal.class));
    verifyNoInteractions(spySin);
    verifyNoInteractions(spyCos);
    verifyNoInteractions(spySec);
    verifyNoInteractions(spyCsc);
    verifyNoInteractions(spyTan);
  }

  @ParameterizedTest(name = "f({0}) = {1}")
  @DisplayName("Test 3: Call function")
  @CsvFileSource(resources = "/integration/systemIT.csv", numLinesToSkip = 1, delimiter = ',')
  void shouldCalculateWithMockFunctions(BigDecimal x, BigDecimal y) {
    if (x.compareTo(ZERO) > 0) {
      when(mockLn.calculate(eq(x), any(BigDecimal.class))).thenReturn(BigDecimal.valueOf(Math.log(x.doubleValue())));
      when(mockLog2.calculate(eq(x), any(BigDecimal.class)))
          .thenReturn(BigDecimal.valueOf(Math.log(x.doubleValue()) / Math.log(2)));
      when(mockLog3.calculate(eq(x), any(BigDecimal.class)))
          .thenReturn(BigDecimal.valueOf(Math.log(x.doubleValue()) / Math.log(3)));
      when(mockLg.calculate(eq(x), any(BigDecimal.class))).thenReturn(BigDecimal.valueOf(Math.log10(x.doubleValue())));
    } else {
      when(mockSin.calculate(eq(x), any(BigDecimal.class))).thenReturn(BigDecimal.valueOf(Math.sin(x.doubleValue())));
      when(mockCos.calculate(eq(x), any(BigDecimal.class))).thenReturn(BigDecimal.valueOf(Math.cos(x.doubleValue())));
      when(mockSec.calculate(eq(x), any(BigDecimal.class)))
          .thenReturn(BigDecimal.valueOf(1 / Math.cos(x.doubleValue())));
      when(mockCsc.calculate(eq(x), any(BigDecimal.class)))
          .thenReturn(BigDecimal.valueOf(1 / Math.sin(x.doubleValue())));
      when(mockTan.calculate(eq(x), any(BigDecimal.class))).thenReturn(BigDecimal.valueOf(Math.tan(x.doubleValue())));
    }
    EquationSystem system = new EquationSystem(mockSin, mockCos, mockSec, mockCsc, mockTan, mockLn, mockLog2, mockLog3, mockLg);
    assertEquals(y, system.calculate(x, PRECISION));
  }
}
