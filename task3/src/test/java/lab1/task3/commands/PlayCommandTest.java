package lab1.task3.commands;

import lab1.task3.models.Species;
import lab1.task3.models.Thing;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class PlayCommandTest {

    @Test
    void testPlayCommandExecution() {
        Species dolphins = new Species("Дельфины");
        Thing water = new Thing("воде");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        PlayCommand command = new PlayCommand(dolphins, "плескались", water);
        command.execute();

        String expectedOutput = "Дельфины плескались в воде.\n";
        assertEquals(expectedOutput.trim(), outputStream.toString().trim());
    }
}