package it.polimi.ingsw.model;

import it.polimi.ingsw.commons.Component;
import it.polimi.ingsw.exceptions.BuildLowerComponentException;
import it.polimi.ingsw.exceptions.RemoveGroundLevelException;

public interface Constructor {
    void addComponent(Component component) throws BuildLowerComponentException;
    Component removeComponent() throws RemoveGroundLevelException;
}
