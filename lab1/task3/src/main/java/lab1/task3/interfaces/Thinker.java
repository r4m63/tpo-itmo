package lab1.task3.interfaces;

import lab1.task3.models.Species;

public interface Thinker {
    void assumeSuperiority(Species other, String reason);

    String getLastThought();
}