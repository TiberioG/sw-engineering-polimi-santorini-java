package it.polimi.ingsw.exceptions;

/**
 * This Exception is thrown if there aren't available cells where the worker can build
 */
public class ZeroCellsAvailableBuildException extends SantoriniException {

    @Override
    public String toString() {
        return "There aren't available cell where this worker can build.\n";
    }
}
