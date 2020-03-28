package it.polimi.ingsw.exceptions;

/**
 * This Exception is thrown if you try to set a player or retrieve a playar does'not exits in the match
 */
public class PlayerNotPresentException extends SantoriniException {
    @Override
    public String toString() {
        return "Player doesn't exists in current match";
    }
}
