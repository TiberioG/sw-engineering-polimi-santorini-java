package it.polimi.ingsw.commons;

public enum ModelEventName {
    PLAYER_LIST_ADDED(0);

    private final int modelEventName;

    ModelEventName(int modelEventName) {
        this.modelEventName = modelEventName;
    }

    public int getModelEventName() {
        return modelEventName;
    }
}
