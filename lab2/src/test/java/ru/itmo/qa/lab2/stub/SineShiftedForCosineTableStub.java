package ru.itmo.qa.lab2.stub;

// Stores values in mutually dependent points x = pi/2 - arg for cosine integration.
public class SineShiftedForCosineTableStub extends CsvTableStub {
  public SineShiftedForCosineTableStub() {
    super("/integration/sineForCosineStub.csv");
  }
}
