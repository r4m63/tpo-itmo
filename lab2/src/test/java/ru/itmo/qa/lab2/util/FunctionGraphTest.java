package ru.itmo.qa.lab2.util;

import org.jfree.data.xy.XYDataset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;

import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;

import javax.swing.JOptionPane;

public class FunctionGraphTest {

  @TempDir
  Path tempDir;

  private File createTestCsv(String content) throws Exception {
    File csvFile = tempDir.resolve("test.csv").toFile();
    try (FileWriter writer = new FileWriter(csvFile)) {
      writer.write(content);
    }
    return csvFile;
  }

  @Test
  @DisplayName("Should create dataset from valid CSV")
  void testCreateDatasetWithValidCsv() throws Exception {
    String csvContent = "x,y\n1.0,2.5\n2.0,3.7\n3.0,1.2\n";
    File csvFile = createTestCsv(csvContent);

    XYDataset dataset = FunctionGraph.createDataset(csvFile.getAbsolutePath());

    assertEquals(3, dataset.getItemCount(0));
    assertEquals(1.0, dataset.getXValue(0, 0));
    assertEquals(2.5, dataset.getYValue(0, 0));
    assertEquals(3.0, dataset.getXValue(0, 2));
    assertEquals(1.2, dataset.getYValue(0, 2));
  }

  @Test
  @DisplayName("Should handle malformed CSV data")
  void testCreateDatasetWithMalformedData() throws Exception {
    String csvContent = "x,y\n1.0,invalid\n2.0,3.7\n";
    File csvFile = createTestCsv(csvContent);

    XYDataset dataset = FunctionGraph.createDataset(csvFile.getAbsolutePath());

    assertEquals(1, dataset.getItemCount(0)); // Only valid row added
    assertEquals(2.0, dataset.getXValue(0, 0));
    assertEquals(3.7, dataset.getYValue(0, 0));
  }

  @Test
  @DisplayName("Should create chart with correct properties")
  void testCreateChart() throws Exception {
    String csvContent = "x,y\n1.0,1.0\n2.0,2.0\n";
    File csvFile = createTestCsv(csvContent);
    XYDataset dataset = FunctionGraph.createDataset(csvFile.getAbsolutePath());

    var chart = FunctionGraph.createChart(dataset, "Test Chart");

    assertEquals("Test Chart", chart.getTitle().getText());
    assertEquals("X", chart.getXYPlot().getDomainAxis().getLabel());
    assertEquals("Y", chart.getXYPlot().getRangeAxis().getLabel());
    assertEquals(Color.WHITE, chart.getXYPlot().getBackgroundPaint());
  }

  @Test
  @DisplayName("Should handle missing CSV file")
  void testMissingCsvFile() {
    try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
      mocked.when(() -> JOptionPane.showMessageDialog(
          any(), anyString(), anyString(), anyInt()))
          .thenAnswer(inv -> null);

      XYDataset dataset = FunctionGraph.createDataset("nonexistent.csv");
      assertEquals(0, dataset.getItemCount(0));
    }
  }

  @Test
  @DisplayName("Should display chart without errors")
  void testDisplayChart() throws Exception {
    String csvContent = "x,y\n1.0,1.0\n2.0,4.0\n3.0,9.0\n";
    File csvFile = createTestCsv(csvContent);

    assertDoesNotThrow(() -> FunctionGraph.displayChart(csvFile.getAbsolutePath()));
  }
}
