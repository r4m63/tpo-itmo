package lab1.task3.models;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ThingTest {

    @Test
    @DisplayName("Вещь наследует имя доменной сущности")
    void testThingName() {
        Thing wheel = new Thing("колесо");

        assertEquals("колесо", wheel.getName());
    }
}
