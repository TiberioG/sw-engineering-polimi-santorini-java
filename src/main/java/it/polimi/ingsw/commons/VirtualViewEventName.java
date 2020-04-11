package it.polimi.ingsw.commons;

public enum VirtualViewEventName {
    PLAYER_INFORMATION_ADDED(0),
    CARDS_IN_GAME_SELECTED(1);

    private final int virtualViewEventName;

    VirtualViewEventName(int virtualViewEventName) {
        this.virtualViewEventName = virtualViewEventName;
    }

    public int getVirtualViewEventName() {
        return virtualViewEventName;
    }
}
