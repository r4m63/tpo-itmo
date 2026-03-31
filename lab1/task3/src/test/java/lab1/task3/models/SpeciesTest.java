package lab1.task3.models;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpeciesTest {

    @Test
    @DisplayName("Вид создается с именем и пустой последней мыслью")
    void testSpeciesCreation() {
        Species human = new Species("Человек");
        assertEquals("Человек", human.getName());
        assertEquals("", human.getLastThought());
    }

    @Test
    @DisplayName("Предположение о превосходстве обновляет мысль вида")
    void testAssumeSuperiorityUpdatesThought() {
        Species human = new Species("Человек");
        Species dolphin = new Species("Дельфин");

        human.assumeSuperiority(dolphin, "причина");

        assertEquals("Мы разумнее, чем Дельфин", human.getLastThought());
    }

    @Test
    @DisplayName("Мысль одного вида не изменяет состояние другого вида")
    void testAssumeSuperiorityDoesNotChangeOtherSpeciesThought() {
        Species humans = new Species("Люди");
        Species dolphins = new Species("Дельфины");

        dolphins.assumeSuperiority(humans, "по той же самой причине");

        assertEquals("Мы разумнее, чем Люди", dolphins.getLastThought());
        assertEquals("", humans.getLastThought());
    }
}
