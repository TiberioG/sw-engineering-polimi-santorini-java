package it.polimi.ingsw.exceptions;

/**
 * This Exception is thrown if you try to remove the ground level of a tower
 */
public class RemoveGroundLevelException extends SantoriniException {
    @Override
    public String toString() {
        return "You can not remove the ground level";
    }
}

