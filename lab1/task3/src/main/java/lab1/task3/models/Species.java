package lab1.task3.models;

import lab1.task3.interfaces.Thinker;

// Аналог Person, но для Видов (Люди, Дельфины)
public class Species extends DomainEntity implements Thinker {

    private String lastThought;

    public Species(String name) {
        super(name);
        this.lastThought = "";
    }

    @Override
    public void assumeSuperiority(Species other, String reason) {
        this.lastThought = "Мы разумнее, чем " + other.getName();
        System.out.println(getName() + " предполагали, что они разумнее " + other.getName() + ", " + reason + ".");
    }

    @Override
    public String getLastThought() {
        return lastThought;
    }
}