package it.polimi.ingsw.psp40.commons.messages;

public enum TypeOfMessage {
    GENERIC_MESSAGE,
    HEARTBEAT,
    SERVER_LOST,
    DISCONNECTED_SERVER_SIDE,
    LOGIN_SUCCESSFUL,
    LOGIN_FAILURE,
    NUM_PLAYERS_FAILURE, // todo implementarlo client side
    LOBBY_CREATED,
    CREATED_MATCH,      //obj_type : null
    START_MATCH("startMatch"),      //obj_type : null
    LOGIN,        //obj_type : Tupla(String, Date)
    USER_JOINED,
    HOW_MANY_PLAYERS,
    ADDED_TO_QUEUE,

    CHOOSE_GAME_CARDS,
    CHOOSE_PLAYERS_CARD,
    CHOOSE_PERSONAL_CARD,
    FORCED_CARD,
    CHOOSE_FIRST_PLAYER,
    CHOOSE_POSITION_OF_WORKERS,

    SET_CARDS_TO_GAME("setCardToGame"),
    SETTED_CARDS_TO_GAME,
    SET_CARD_TO_PLAYER("setCardToPlayer"),
    SET_FIRST_PLAYER("setFirstPlayer"),
    SET_POSITION_OF_WORKER("setPositionOfWorker"),
    INIT_TURN,
    SELECT_WORKER("selectWorker"),
    NEXT_PHASE_AVAILABLE,
    RETRIEVE_CELL_FOR_MOVE("retrieveCellForMove"),
    AVAILABLE_CELL_FOR_MOVE,
    MOVE_WORKER("moveWorker"),
    RETRIEVE_CELL_FOR_BUILD("retrieveCellForBuild"),
    AVAILABLE_CELL_FOR_BUILD,
    BUILD_CELL("buildCell"),
    INIT_NEW_MATCH,

    TOWER_UPDATED,
    ISLAND_UPDATED,
    LIST_PLAYER_UPDATED,
    PLAYER_UPDATED,
    SET_WORKERS_COLOR,
    LOCATION_UPDATED,  //obj_type : Location
    WINNING_PLATER_UPDATED,
    PLAYER_HAS_LOST;

    private String typeOfMessage;

    TypeOfMessage(String typeOfMessage) {
        this.typeOfMessage = typeOfMessage;
    }

    TypeOfMessage() {

    }

    public String toString() {
        return typeOfMessage;
    }
}
