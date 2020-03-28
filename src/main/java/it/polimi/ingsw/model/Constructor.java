package it.polimi.ingsw.model;

import it.polimi.ingsw.commons.Component;

public interface Constructor {
    void addLevel(Component component);
    Component removeLevel();
}
