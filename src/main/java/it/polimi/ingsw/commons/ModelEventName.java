package it.polimi.ingsw.commons;

public enum ModelEventName {
    ADDED_PLAYER_IN_LIST(0),
    CURRENT_PLAYER_SETTED(1);


    private final int modelEventName;

    ModelEventName(int modelEventName) {
        this.modelEventName = modelEventName;
    }

    public int getModelEventName() {
        return modelEventName;
    }
}
