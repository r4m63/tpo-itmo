package lab1.task3.controllers;

import lab1.task3.commands.InventCommand;
import lab1.task3.models.Species;
import lab1.task3.models.Thing;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class StoryScenarioTest {

    @Test
    void testScenarioExecution() {
        Species human = new Species("Человек");
        Thing war = new Thing("войну");

        var inventCommand = new InventCommand(human, war);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        StoryScenario scenario = new StoryScenario();
        scenario.addCommand(inventCommand);
        scenario.execute();

        String expectedOutput = "--- НАЧАЛО ИСТОРИИ ---\n" +
                "* Человек придумали войну.\n" +
                "--- КОНЕЦ ИСТОРИИ ---";

        // Используем normalize line separators, если у вас Java 13+, или просто trim/replace
        assertEquals(expectedOutput.replace("\r", "").trim(), outputStream.toString().replace("\r", "").trim());
    }
}