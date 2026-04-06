package ru.itmo.qa.lab2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

// Проверяет сценарии запуска Main как пользовательской точки входа:
// экспорт по умолчанию, CLI-параметры, вывод списка модулей и обработку ошибок.
public class MainTest {
    @TempDir
    Path tempDir;

    @BeforeEach
    // Готовит изолированный каталог для CSV перед каждым тестом и перенаправляет
    // туда вывод Main, чтобы проверки не трогали реальные файлы проекта.
    void setUp() {
        Path csvDir = tempDir.resolve("csv");
        try {
            Files.createDirectories(csvDir);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create test directory", e);
        }

        Main.setOutputDir(csvDir.toString());
    }

    @Test
    // Проверяет поведение по умолчанию: запуск без аргументов должен экспортировать
    // CSV для всех основных модулей и системы, причем файлы не должны быть пустыми.
    @DisplayName("Main should generate CSV files for all functions")
    void shouldGenerateFunctionDataFiles() throws IOException {
        Main.main(new String[] {});

        String[] expectedFiles = {
                "Sine.csv", "Cosine.csv", "Secant.csv", "NaturalLogarithm.csv",
                "BaseNLogarithm_2.csv", "BaseNLogarithm_3.csv", "BaseNLogarithm_10.csv",
                "EquationSystem.csv"
        };

        for (String filename : expectedFiles) {
            Path filePath = tempDir.resolve("csv" + File.separator + filename);
            assertTrue(Files.exists(filePath), "Missing file: " + filename);
            assertTrue(Files.size(filePath) > 0, "Empty file: " + filename);
        }
    }

    @Test
    // Проверяет минимальную валидность содержимого CSV на примере синуса:
    // файл должен быть создан в ожидаемом формате и начинаться с заголовка.
    @DisplayName("CSV files should contain valid data")
    void testCsvContent() throws IOException {
        Main.main(new String[] {});

        Path sineFile = tempDir.resolve("csv" + File.separator + "Sine.csv");
        String content = Files.readString(sineFile);

        assertTrue(content.startsWith("x,y"));
    }

    @Test
    // Проверяет пользовательский сценарий CLI: экспорт только выбранного модуля
    // с заданными границами, шагом, точностью и собственным каталогом вывода.
    @DisplayName("Should export selected module with custom step")
    void shouldExportSelectedModuleWithCustomStep() throws IOException {
        Main.main(new String[] {
                "--module", "system",
                "--from", "-1",
                "--to", "1",
                "--step", "0.5",
                "--precision", "0.0000001",
                "--output-dir", tempDir.resolve("custom").toString()
        });

        Path exported = tempDir.resolve("custom" + File.separator + "EquationSystem.csv");
        assertTrue(Files.exists(exported));

        var lines = Files.readAllLines(exported);
        assertEquals("x,y", lines.get(0));
        assertEquals(6, lines.size());
        assertTrue(lines.stream().anyMatch(line -> line.startsWith("-1,")));
        assertTrue(lines.stream().anyMatch(line -> line.startsWith("0.5,")));
    }

    @Test
    // Проверяет специальную нормализацию путей: директории вида /tmp/... должны
    // проецироваться во внутренний tmp-каталог проекта, а не во внешний системный /tmp.
    @DisplayName("Should map /tmp output dir into project tmp directory")
    void shouldExportIntoProjectTmpDirectory() throws IOException {
        Path projectTmpDir = Path.of(System.getProperty("user.dir"), "tmp", "lab2-demo-test");
        Files.createDirectories(projectTmpDir);

        try {
            Main.main(new String[] {
                    "--module", "system",
                    "--from", "-1",
                    "--to", "1",
                    "--step", "0.5",
                    "--precision", "0.0000001",
                    "--output-dir", "/tmp/lab2-demo-test"
            });

            Path exported = projectTmpDir.resolve("EquationSystem.csv");
            assertTrue(Files.exists(exported));
            assertTrue(Files.readString(exported).startsWith("x,y"));
        } finally {
            Files.deleteIfExists(projectTmpDir.resolve("EquationSystem.csv"));
            Files.deleteIfExists(projectTmpDir);
        }
    }

    @Test
    // Проверяет сервисный режим --list-modules: Main должен вывести список
    // доступных идентификаторов модулей, которые можно передавать в CLI.
    @DisplayName("Should print available modules")
    void shouldPrintAvailableModules() {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();

        try {
            System.setOut(new PrintStream(outContent));
            Main.main(new String[] { "--list-modules" });
            String output = outContent.toString();
            assertTrue(output.contains("sin"));
            assertTrue(output.contains("system"));
            assertTrue(output.contains("log10"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    // Проверяет, что IOException внутри main не "роняет" программу молча,
    // а приводит к диагностическому сообщению в stderr.
    @DisplayName("Should handle IOException in main method")
    void testMainWithIOException() {
        PrintStream originalErr = System.err;

        try {
            ByteArrayOutputStream errContent = new ByteArrayOutputStream();
            System.setErr(new PrintStream(errContent));

            var main = new Main();
            main.setOutputDir("/invalid/path/");
            main.main(new String[] {});

            assertTrue(errContent.toString().contains("Ошибка при работе с файлами"));
        } finally {
            System.setErr(originalErr);
        }
    }
}
