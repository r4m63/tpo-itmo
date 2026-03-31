package lab1.task3.commands;

import lab1.task3.models.Species;
import lab1.task3.models.Thing;

import java.util.Objects;

public class InventCommand implements Command {
    private final Species subject;
    private final Thing invention;

    public InventCommand(Species subject, Thing invention) {
        this.subject = Objects.requireNonNull(subject);
        this.invention = Objects.requireNonNull(invention);
    }

    @Override
    public void execute() {
        System.out.println(subject.getName() + " придумали " + invention.getName() + ".");
    }
}