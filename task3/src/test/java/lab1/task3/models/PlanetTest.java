package lab1.task3.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlanetTest {
    @Test
    void testPlanetInhabitants() {
        Planet earth = new Planet("Земля");
        Species human = new Species("Люди");

        earth.addInhabitant(human);

        assertEquals(1, earth.getInhabitants().size());
        assertEquals("Люди", earth.getInhabitants().get(0).getName());
    }
}