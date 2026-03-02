package lab1.task3.commands;

import lab1.task3.models.Species;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class AssumeSuperiorityCommandTest {

    @Test
    void testAssumeSuperiorityExecution() {
        Species humans = new Species("Люди");
        Species dolphins = new Species("Дельфины");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        AssumeSuperiorityCommand command = new AssumeSuperiorityCommand(humans, dolphins, "потому что изобрели колесо");
        command.execute();

        String expectedOutput = "Люди предполагали, что они разумнее Дельфины, потому что изобрели колесо.\n";
        assertEquals(expectedOutput.trim(), outputStream.toString().trim());

        // Также проверяем состояние модели
        assertEquals("Мы разумнее, чем Дельфины", humans.getLastThought());
    }
}