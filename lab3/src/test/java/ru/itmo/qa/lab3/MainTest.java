package ru.itmo.qa.lab3;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MainTest {
    @Test
    @DisplayName("Main should print lab instructions")
    void shouldPrintLabInstructions() {
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
