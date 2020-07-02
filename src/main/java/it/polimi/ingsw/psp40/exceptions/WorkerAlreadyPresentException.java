package it.polimi.ingsw.psp40.exceptions;

/**
 * This Exception is thrown if you try to put two workers on a single cell
 */
public class WorkerAlreadyPresentException extends SantoriniException {

    @Override
    public String toString() {
        return "You can't have two workers on a single cell .\n";
    }
}
