package ru.itmo.qa.lab2.trig.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itmo.qa.lab2.trig.Cosecant;
import ru.itmo.qa.lab2.trig.Sine;

import java.math.BigDecimal;

import static java.math.RoundingMode.HALF_EVEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CosecantIntegrationTest {

  private static final BigDecimal PRECISION = new BigDecimal("0.0000001");

  @Mock
  private Sine mockSin;

  @Spy
  private Sine spySin;

  @Test
  @DisplayName("Test 1: Call sine")
  void shouldCallSineFunction() {
    Cosecant csc = new Cosecant(spySin);
    csc.calculate(new BigDecimal(965), PRECISION);
    verify(spySin, atLeastOnce()).calculate(any(BigDecimal.class), any(BigDecimal.class));
  }

  @DisplayName("Test 2: Call cosecant")
  @ParameterizedTest(name = "mock.csc({0}) = {1}")
  @CsvFileSource(resources = "/integration/cscIT.csv", numLinesToSkip = 1, delimiter = ',')
  void shouldCallCosecantFunction(BigDecimal x, BigDecimal y) {
    when(mockSin.calculate(x, PRECISION.setScale(PRECISION.scale() + 12, HALF_EVEN)))
        .thenReturn(BigDecimal.valueOf(Math.sin(x.doubleValue())));
    Cosecant csc = new Cosecant(mockSin);
    assertEquals(y, csc.calculate(x, PRECISION));
  }
}
