package lab1.task3.commands;

import lab1.task3.models.Species;
import lab1.task3.models.Thing;

import java.util.Objects;

public class PlayCommand implements Command {
    private final Species subject;
    private final String action; // "плескались", "развлекались"
    private final Thing context; // "вода"

    public PlayCommand(Species subject, String action, Thing context) {
        this.subject = Objects.requireNonNull(subject);
        this.action = Objects.requireNonNull(action);
        this.context = Objects.requireNonNull(context); // может быть null, если контекст не важен, но по тексту "в воде"
    }

    @Override
    public void execute() {
        System.out.println(subject.getName() + " " + action + " в " + context.getName() + ".");
    }
}