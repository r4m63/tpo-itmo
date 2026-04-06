package ru.itmo.qa.lab2.util;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static java.math.BigDecimal.ZERO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.itmo.qa.lab2.function.AbstractFunction;

@ExtendWith(MockitoExtension.class)
class CSVGraphWriterTest {
    @TempDir
    Path tempDir;

    private CSVGraphWriter writer;

    @Mock
    private AbstractFunction mockFunction;

    private String defaultDir() {
        return tempDir.resolve("csv").toString() + File.separator;
    }

    @Test
    @DisplayName("Должен создавать CSV-файл для функции")
    void shouldCreateFile() throws IOException {
        writer = new CSVGraphWriter(mockFunction, defaultDir());
        File expectedFile = new File(defaultDir() + mockFunction.getClass().getSimpleName() + ".csv");

        assertTrue(expectedFile.exists(), "Файл должен быть создан");
    }

    @Test
    @DisplayName("Должен записывать значения функции в CSV-файл")
    void shouldWriteToFile() throws IOException {
        when(mockFunction.calculate(any(), any())).thenReturn(ONE);

        writer = new CSVGraphWriter(mockFunction, defaultDir());
        writer.write(ZERO, TEN, ONE, BigDecimal.valueOf(0.01));

        File file = new File(defaultDir() + mockFunction.getClass().getSimpleName() + ".csv");
        List<String> lines = Files.readAllLines(file.toPath());

        assertEquals("x,y", lines.get(0));
        assertEquals("0,1", lines.get(1));
    }

    @Test
    @DisplayName("Должен оставлять пустую строку при разрыве функции")
    void shouldHandleArithmeticException() throws IOException {
        when(mockFunction.calculate(any(), any())).thenThrow(new ArithmeticException("Разрыв"));

        writer = new CSVGraphWriter(mockFunction, defaultDir());
        writer.write(BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.valueOf(0.01));

        File file = new File(defaultDir() + mockFunction.getClass().getSimpleName() + ".csv");
        List<String> lines = Files.readAllLines(file.toPath());

        assertTrue(lines.get(1).isEmpty(), "Должна быть пустая строка при разрыве");
        assertEquals("x,y", lines.get(0));
    }

    @Test
    @DisplayName("Должен вызывать flush у writer после записи")
    void shouldCallFlush() throws IOException {
        mockFunction = mock(AbstractFunction.class);

        BufferedWriter mockWriter = mock(BufferedWriter.class);

        writer = new CSVGraphWriter(mockWriter, defaultDir(), mockFunction);
        writer.write(BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.valueOf(0.01));

        verify(mockWriter, atLeastOnce()).flush();
    }

    @Test
    @DisplayName("Должен отклонять неположительный шаг при записи CSV")
    void shouldRejectNonPositiveStep() throws IOException {
        writer = new CSVGraphWriter(mockFunction, defaultDir());
        var exception = org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> writer.write(BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.valueOf(0.01)));

        assertEquals("Шаг должен быть больше нуля", exception.getMessage());
    }

    @AfterEach
    void tearDown() {
        writer = null;
        File file = new File(defaultDir() + mockFunction.getClass().getSimpleName() + ".csv");
        if (!(file.delete())) {
            System.out.println("Файл удален.");
        } else {
            System.out.println("Файл не удален.");
        }
    }
}
