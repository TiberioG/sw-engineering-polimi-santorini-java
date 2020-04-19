package it.polimi.ingsw.commons.messages;

public enum TypeOfMessage {
    MOVE_WORKER(0),
    CREATE_MATCH(3),      //obj_type : null
    ADD_PLAYER(4),        //obj_type : Tupla(String, Date)
    LOCATION_UPDATED(5),  //obj_type : Location
    SET_PLAYER_COLOR(6),  //obj_type : Tupla(Player, Color)
    CARDS_GET_ALL(7),     //obj_type : List<Int>
    CARDS_SET_GAME(8),
    CARD_SET_PLAYER(9),
    SET_WORKER_COLOR(10),
    SET_WORKER_POSITION(11),
    SELECT_WORKER(12),
    LOGIN_SUCCESSFUL(13),
    LOGIN_FAILURE(14),
    CREATED_MATCH(15);      //obj_type : null
    ;


    private final int typeOfMessage;

    TypeOfMessage(int typeOfMessage) {
        this.typeOfMessage = typeOfMessage;
    }

}
