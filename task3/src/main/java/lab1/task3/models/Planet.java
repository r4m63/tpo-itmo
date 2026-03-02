package lab1.task3.models;

import java.util.ArrayList;
import java.util.List;

// Контекст (Планета)
public class Planet extends DomainEntity {
    private final List<Species> inhabitants;

    public Planet(String name) {
        super(name);
        this.inhabitants = new ArrayList<>();
    }

    public void addInhabitant(Species species) {
        inhabitants.add(species);
    }

    public List<Species> getInhabitants() {
        return inhabitants;
    }
}