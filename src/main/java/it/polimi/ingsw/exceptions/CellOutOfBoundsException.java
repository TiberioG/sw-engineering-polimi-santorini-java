package it.polimi.ingsw.exceptions;

/**
 * This Exception is thrown if you try to get a cell with a coordinate out of island's bounds
 */
public class CellOutOfBoundsException extends SantoriniException {
    @Override
    public String toString() {
        return "You entered a coordinate out of bounds.\n";
    }
}
