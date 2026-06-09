package ru.itmo.qa.lab3;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// Smoke-тест для класса Main, который выводит инструкции к лабораторной.
// Не помечен @Tag("ui"), поэтому исполняется задачей Gradle test (а не uiTest)
// и не требует браузера. Цель проверить, что вывод main содержит ключевые маркеры:
// упоминание лабораторной, ссылку на use case-диаграмму и команду запуска UI-тестов.
class MainTest {
    @Test
    @DisplayName("Main should print lab instructions")
    void shouldPrintLabInstructions() {
        // Подменяем System.out на буфер, чтобы перехватить вывод main и проверить его содержимое.
        // Оригинальный поток восстанавливаем в finally, чтобы не сломать другие тесты.
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {
            System.setOut(new PrintStream(output));

            Main.main(new String[] {});

            String text = output.toString();
            assertTrue(text.contains("Lab 3 is configured for UI testing of Subscribe.ru."));
            assertTrue(text.contains("lab3/docs/use-case-diagram.puml"));
            assertTrue(text.contains("./gradlew :lab3:uiTest"));
        } finally {
            System.setOut(originalOut);
        }
    }
}
