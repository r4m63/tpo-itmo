package ru.itmo.qa.lab2.util;

import ru.itmo.qa.lab2.function.AbstractFunction;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;

public class CSVGraphWriter {
  private final BufferedWriter writer;
  private final AbstractFunction function;
  private final String filePath;

  public CSVGraphWriter(AbstractFunction function, String outputDir) throws IOException {
    this.function = function;
    this.filePath = getFilePath(outputDir, function);
    this.writer = createWriter();
  }

  public CSVGraphWriter(BufferedWriter writer, String outputDir, AbstractFunction function) {
    this.function = function;
    this.filePath = getFilePath(outputDir, function);
    this.writer = writer;
  }

  public String getFilePath() {
    return filePath;
  }

  private String getFilePath(String outputDir, AbstractFunction function) {
    final String functionName = function.getName() == null || function.getName().isBlank()
        ? function.getClass().getSimpleName()
        : function.getName();
    return outputDir + functionName + ".csv";
  }

  private BufferedWriter createWriter() throws IOException {
    File file = new File(filePath);
    file.getParentFile().mkdirs();
    if (file.exists()) {
      return new BufferedWriter(new FileWriter(file, false));
    } else {
      file.createNewFile();
      return new BufferedWriter(new FileWriter(file));
    }
  }

  public void write(BigDecimal x1, BigDecimal x2, BigDecimal d, BigDecimal precision) throws IOException {
    validateRange(x1, x2, d, precision);

    try {
      writer.write("x,y");
      writer.newLine();
      for (BigDecimal i = x1; i.compareTo(x2) <= 0; i = i.add(d)) {
        try {
          BigDecimal y = function.calculate(i, precision);
          if (y != null) {
            writer.write(toCsvValue(i));
            writer.write(",");
            writer.write(toCsvValue(y));
            writer.newLine();
          } else {
            writer.newLine();
          }
        } catch (ArithmeticException e) {
          writer.newLine();
        }
      }
    } finally {
      writer.flush();
    }
  }

  private void validateRange(BigDecimal x1, BigDecimal x2, BigDecimal step, BigDecimal precision) {
    if (x1 == null || x2 == null || step == null || precision == null) {
      throw new IllegalArgumentException("Параметры диапазона и точности не должны быть null");
    }
    if (step.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Шаг должен быть больше нуля");
    }
    if (x1.compareTo(x2) > 0) {
      throw new IllegalArgumentException("Начало диапазона должно быть не больше конца");
    }
  }

  private String toCsvValue(BigDecimal value) {
    return value.stripTrailingZeros().toPlainString();
  }
}
