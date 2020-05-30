package it.polimi.ingsw.psp40.commons;

public enum PhaseType {
    SELECT_WORKER ("Select one worker"),
    MOVE_WORKER("Move your worker"),
    BUILD_COMPONENT("Build a component"),
    END_TURN ("End your turn"),
    UNRECOGNIZED_PHASE("Booh");


    private final String prettyName;

    PhaseType(String prettyName) {
        this.prettyName = prettyName;
    }

    public String getPrettyName() {
        return prettyName;
    }
}
