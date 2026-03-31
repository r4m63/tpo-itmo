package ru.itmo.qa.lab2.log.integration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itmo.qa.lab2.log.BaseNLogarithm;
import ru.itmo.qa.lab2.log.NaturalLogarithm;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogarithmIntegrationTest {

  private static final BigDecimal PRECISION = new BigDecimal("0.0000001");

  @Mock
  private NaturalLogarithm mockLn;
  @Spy
  private NaturalLogarithm spyLn;

  @Test
  void shouldCallLn() {
    final BaseNLogarithm logarithm = new BaseNLogarithm(5, spyLn);
    logarithm.calculate(new BigDecimal(993), new BigDecimal("0.001"));
    verify(spyLn, atLeastOnce()).calculate(any(BigDecimal.class), any(BigDecimal.class));
  }

  @Test
  void shouldCalculateWithMockLn() {
    final BigDecimal arg = new BigDecimal(1488);
    when(mockLn.calculate(eq(new BigDecimal(1488)), any(BigDecimal.class))).thenReturn(new BigDecimal("7.3051882"));
    when(mockLn.calculate(eq(new BigDecimal(5)), any(BigDecimal.class))).thenReturn(new BigDecimal("1.6094379"));

    final BaseNLogarithm log5 = new BaseNLogarithm(5, mockLn);
    final BigDecimal expected = new BigDecimal("4.5389687");
    assertEquals(expected, log5.calculate(arg, PRECISION));
  }
}
