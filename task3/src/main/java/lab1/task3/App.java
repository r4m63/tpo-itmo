package lab1.task3;

import lab1.task3.commands.*;
import lab1.task3.controllers.StoryScenario;
import lab1.task3.models.*;

public class App {
    public static void main(String[] args) {
        // Создаем мир
        var earth = new Planet("Земля");
        var humans = new Species("Люди");
        var dolphins = new Species("Дельфины");

        earth.addInhabitant(humans);
        earth.addInhabitant(dolphins);

        // Создаем объекты
        var wheel = new Thing("колесо");
        var ny = new Thing("Нью-Йорк");
        var war = new Thing("войну");
        var water = new Thing("воде");

        // Формируем сценарий
        var scenario = new StoryScenario() {{
            // Люди что-то придумали
            addCommand(new InventCommand(humans, wheel));
            addCommand(new InventCommand(humans, ny));
            addCommand(new InventCommand(humans, war));

            // Люди сделали вывод
            addCommand(new AssumeSuperiorityCommand(humans, dolphins, "потому что они придумали так много"));

            // Дельфины развлекались
            addCommand(new PlayCommand(dolphins, "плескались", water));
            addCommand(new PlayCommand(dolphins, "развлекались", water)); // упростим контекст до воды

            // Дельфины сделали вывод (по той же причине)
            addCommand(new AssumeSuperiorityCommand(dolphins, humans, "причем, по той же самой причине"));
        }};

        scenario.execute();
    }
}