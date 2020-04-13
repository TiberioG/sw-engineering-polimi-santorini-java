package it.polimi.ingsw.commons;

public enum TypeOfMessage {
    MOVE_WORKER(0),
    CREATED_PLAYER(1),    //obj_type : Player
    CREATE_MATCH(3),      //obj_type : null
    ADD_PLAYER(4),       //obj_type : Tupla(String, Date)
    LOCATION_UPDATED(5), //obj_type : Location
    SET_PLAYER_COLOR(6); //obj_type : Tupla(Player, Color)


    private final int typeOfMessage;

    TypeOfMessage(int typeOfMessage) {
        this.typeOfMessage = typeOfMessage;
    }

}
