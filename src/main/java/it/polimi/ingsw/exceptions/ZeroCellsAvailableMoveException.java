package it.polimi.ingsw.exceptions;

/**
 * This Exception is thrown if there aren't available cells where move the worker
 */
public class ZeroCellsAvailableMoveException extends SantoriniException {

    @Override
    public String toString() {
        return "There aren't available cell where move this worker.\n";
    }
}
