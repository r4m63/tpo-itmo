package lab1.task3.commands;

import lab1.task3.models.Species;
import lab1.task3.models.Thing;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class InventCommandTest {

    @Test
    @DisplayName("Команда InventCommand выводит факт изобретения")
    void testInventCommandExecution() {
        Species humans = new Species("Люди");
        Thing wheel = new Thing("колесо");

        PrintStream originalOut = System.out;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        try {
            InventCommand command = new InventCommand(humans, wheel);
            command.execute();

            String expectedOutput = "Люди придумали колесо.\n";
            assertEquals(expectedOutput.trim(), outputStream.toString().trim());
        } finally {
            System.setOut(originalOut);
        }
    }
}
