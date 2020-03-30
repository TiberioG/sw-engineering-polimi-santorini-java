package it.polimi.ingsw.commons;

/**
 * This is the class for the components of a tower
 * @author sup3rgiu
 */
public enum Component {
    GROUND(0),
    FIRST_LEVEL(1),
    SECOND_LEVEL(2),
    THIRD_LEVEL(3),
    DOME(4);

    private final int componentCode;

    Component(int componentCode) {
        this.componentCode = componentCode;
    }

    public int getComponentCode() {
        return componentCode;
    }
}
