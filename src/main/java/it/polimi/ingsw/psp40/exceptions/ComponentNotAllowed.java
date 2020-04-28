package it.polimi.ingsw.psp40.exceptions;

/**
 * This Exception is thrown if the player selected the wrong type of component to build
 */
public class ComponentNotAllowed extends SantoriniException {

    @Override
    public String toString() {
        return "You can't build this component with your worker here .\n";
    }
}
