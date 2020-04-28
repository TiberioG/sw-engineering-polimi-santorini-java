package it.polimi.ingsw.psp40.exceptions;

/**
 * This Exception is thrown if the player selected a wrong cell where move the worker
 */
public class WrongCellSelectedMoveException extends SantoriniException {

    @Override
    public String toString() {
        return "You can't move this worker here.\n";
    }
}