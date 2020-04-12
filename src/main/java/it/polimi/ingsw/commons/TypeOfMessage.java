package it.polimi.ingsw.commons;

public enum TypeOfMessage {
    MOVE_WORKER(0),
    CREATED_PLAYER(1);;

    private final int typeOfMessage;

    TypeOfMessage(int typeOfMessage) {
        this.typeOfMessage = typeOfMessage;
    }

}
