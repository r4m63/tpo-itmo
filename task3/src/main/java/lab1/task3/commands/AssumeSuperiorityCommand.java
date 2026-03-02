package lab1.task3.commands;

import lab1.task3.interfaces.Thinker;
import lab1.task3.models.Species;

import java.util.Objects;

public class AssumeSuperiorityCommand implements Command {
    private final Species subject;
    private final Species other;
    private final String reason;

    public AssumeSuperiorityCommand(Species subject, Species other, String reason) {
        this.subject = Objects.requireNonNull(subject);
        this.other = Objects.requireNonNull(other);
        this.reason = Objects.requireNonNull(reason);
    }

    @Override
    public void execute() {
        ((Thinker) subject).assumeSuperiority(other, reason);
    }
}