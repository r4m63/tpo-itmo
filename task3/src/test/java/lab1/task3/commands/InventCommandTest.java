package lab1.task3.commands;

import lab1.task3.models.Species;
import lab1.task3.models.Thing;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class InventCommandTest {

    @Test
    void testInventCommandExecution() {
        Species humans = new Species("Люди");
        Thing wheel = new Thing("колесо");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        InventCommand command = new InventCommand(humans, wheel);
        command.execute();

        // Проверяем точный вывод в консоль
        String expectedOutput = "Люди придумали колесо.\n";
        // .trim() используется для игнорирования различий в переводах строк разных ОС,
        // но в примере было toString(), оставим так, но учтите \r\n vs \n
        assertEquals(expectedOutput.trim(), outputStream.toString().trim());
    }
}