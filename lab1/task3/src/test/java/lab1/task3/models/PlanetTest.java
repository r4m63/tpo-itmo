package lab1.task3.models;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlanetTest {
    @Test
    @DisplayName("Планета хранит добавленных обитателей")
    void testPlanetInhabitants() {
        Planet earth = new Planet("Земля");
        Species human = new Species("Люди");

        earth.addInhabitant(human);

        assertEquals(1, earth.getInhabitants().size());
        assertEquals("Люди", earth.getInhabitants().get(0).getName());
    }

    @Test
    @DisplayName("Планета сохраняет порядок добавления видов")
    void testPlanetInhabitantsOrder() {
        Planet earth = new Planet("Земля");
        Species humans = new Species("Люди");
        Species dolphins = new Species("Дельфины");

        earth.addInhabitant(humans);
        earth.addInhabitant(dolphins);

        assertEquals("Земля", earth.getName());
        assertEquals(2, earth.getInhabitants().size());
        assertEquals("Люди", earth.getInhabitants().get(0).getName());
        assertEquals("Дельфины", earth.getInhabitants().get(1).getName());
    }
}
