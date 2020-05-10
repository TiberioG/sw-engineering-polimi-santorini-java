package it.polimi.ingsw.psp40.exceptions;

public class OldUserException extends SantoriniException {

    @Override
    public String toString() {
        return "You're too old to play this game";
    }
}
