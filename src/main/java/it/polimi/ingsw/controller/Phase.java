package it.polimi.ingsw.controller;

import java.util.List;

public class Phase {
    private String type;
    private List<Phase> nextPhases;

    public Phase(String type, List<Phase> nextPhases) {
        this.type = type;
        this.nextPhases = nextPhases;
    }

    public String getType() {
        return type;
    }

    public List<Phase> getNextPhases() {
        return nextPhases;
    }
}
