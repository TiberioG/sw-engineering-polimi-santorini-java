package it.polimi.ingsw.psp40.exceptions;

/**
 * This Exception is thrown if you try to build a component on an upper component (ex: LEVEL1 on LEVEL2)
 */
public class BuildLowerComponentException extends SantoriniException {
    @Override
    public String toString() {
        return "You can not build this component here";
    }
}
