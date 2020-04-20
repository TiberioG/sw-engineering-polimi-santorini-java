package it.polimi.ingsw.commons.messages;

public enum TypeOfMessage {
    MOVE_WORKER(0),
    GENERIC_MESSAGE(1),
    DISCONNECTED_SERVER_SIDE(2),
    START_MATCH(3),      //obj_type : null
    LOGIN(4),        //obj_type : Tupla(String, Date)
    LOCATION_UPDATED(5),  //obj_type : Location
    SET_PLAYER_COLOR(6),  //obj_type : Tupla(Player, Color)
    CHOOSE_GAME_CARDS(7),
    CARDS_SET_GAME(8),
    CARD_SET_PLAYER(9),
    SET_WORKER_COLOR(10),
    SET_WORKER_POSITION(11),
    SELECT_WORKER(12),
    LOGIN_SUCCESSFUL(13),
    LOGIN_FAILURE(14),
    CREATED_MATCH(15),      //obj_type : null
    HOW_MANY_PLAYERS(16),
    USER_JOINED(17),
    ADDED_TO_QUEUE(18),
    TOWER_UPDATED(19),
    ;


    private final int typeOfMessage; //todo kill this

    TypeOfMessage(int typeOfMessage) {
        this.typeOfMessage = typeOfMessage;
    }

}
