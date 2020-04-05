package it.polimi.ingsw.exceptions;

/**
 * This Exception is thrown if the player selected a wrong cell where make the worker build
 */
public class WrongCellSelectedBuildException extends SantoriniException {

    @Override
    public String toString() {
        return "You can't build with your worker  here.\n";
    }
}
