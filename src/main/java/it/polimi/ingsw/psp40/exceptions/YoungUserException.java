package it.polimi.ingsw.psp40.exceptions;

public class YoungUserException extends SantoriniException {

    @Override
    public String toString() {
        return "You're too young to play this game";
    }
}
