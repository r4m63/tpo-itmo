package lab1.task3.controllers;

import lab1.task3.commands.InventCommand;
import lab1.task3.commands.PlayCommand;
import lab1.task3.models.Species;
import lab1.task3.models.Thing;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class StoryScenarioTest {

    @Test
    @DisplayName("Сценарий выполняет добавленные команды между началом и концом истории")
    void testScenarioExecution() {
        Species human = new Species("Человек");
        Thing war = new Thing("войну");

        var inventCommand = new InventCommand(human, war);

        PrintStream originalOut = System.out;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        try {
            StoryScenario scenario = new StoryScenario();
            scenario.addCommand(inventCommand);
            scenario.execute();

            String expectedOutput = "--- НАЧАЛО ИСТОРИИ ---\n" +
                    "* Человек придумали войну.\n" +
                    "--- КОНЕЦ ИСТОРИИ ---";

            assertEquals(expectedOutput.replace("\r", "").trim(), outputStream.toString().replace("\r", "").trim());
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    @DisplayName("Сценарий сохраняет порядок команд из истории")
    void testScenarioPreservesCommandOrder() {
        Species humans = new Species("Люди");
        Species dolphins = new Species("Дельфины");
        Thing wheel = new Thing("колесо");
        Thing water = new Thing("воде");

        PrintStream originalOut = System.out;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        try {
            StoryScenario scenario = new StoryScenario();
            scenario.addCommand(new InventCommand(humans, wheel));
            scenario.addCommand(new PlayCommand(dolphins, "плескались", water));
            scenario.execute();

            String expectedOutput = "--- НАЧАЛО ИСТОРИИ ---\n" +
                    "* Люди придумали колесо.\n" +
                    "* Дельфины плескались в воде.\n" +
                    "--- КОНЕЦ ИСТОРИИ ---";

            assertEquals(expectedOutput.replace("\r", "").trim(), outputStream.toString().replace("\r", "").trim());
        } finally {
            System.setOut(originalOut);
        }
    }
}
