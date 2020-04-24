package it.polimi.ingsw.commons.messages;

public enum TypeOfMessage {
    GENERIC_MESSAGE,
    HEARTBEAT,
    DISCONNECTED_SERVER_SIDE,
    LOGIN_SUCCESSFUL,
    LOGIN_FAILURE,
    NUM_PLAYERS_FAILURE, // todo implementarlo client side
    CREATED_MATCH,      //obj_type : null
    START_MATCH,      //obj_type : null
    LOGIN,        //obj_type : Tupla(String, Date)
    USER_JOINED,
    HOW_MANY_PLAYERS,
    ADDED_TO_QUEUE,

    CHOOSE_GAME_CARDS,
    CHOOSE_PLAYERS_CARD,
    CHOOSE_FIRST_PLAYER,
    CHOOSE_POSITION_OF_WORKERS,
    SET_CARDS_TO_GAME,
    SET_CARD_TO_PLAYER,
    SET_FIRST_PLAYER,
    SET_POSITION_OF_WORKER,
    SELECT_WORKER,
    INIT_TURN,
    RETRIEVE_CELL_FOR_MOVE,
    MOVE_WORKER,
    RETRIEVE_CELL_FOR_BUILD,
    BUILD_CELL,

    TOWER_UPDATED,
    ISLAND_UPDATED,
    PLAYER_UPDATED,
    LOCATION_UPDATED;  //obj_type : Location
}
