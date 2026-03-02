package lab1.task3.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpeciesTest {

    @Test
    void testSpeciesCreation() {
        Species human = new Species("Человек");
        assertEquals("Человек", human.getName());
        assertEquals("", human.getLastThought());
    }

    @Test
    void testAssumeSuperiorityUpdatesThought() {
        Species human = new Species("Человек");
        Species dolphin = new Species("Дельфин");

        human.assumeSuperiority(dolphin, "причина");

        assertEquals("Мы разумнее, чем Дельфин", human.getLastThought());
    }
}