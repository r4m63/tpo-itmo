package lab1.task3.models;

public class DomainEntity {
    private final String name;

    public DomainEntity(String name) {
        this.name = name;
    }

    // Именно этот метод вы пытаетесь вызвать!
    public String getName() {
        return name;
    }
}