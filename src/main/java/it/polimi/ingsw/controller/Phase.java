package it.polimi.ingsw.controller;

import java.util.List;

public class Phase {
    private String type;
    private List<Phase> nextPhases;
    private boolean needCheckForVictory;

    public Phase(String type, List<Phase> nextPhases, boolean needCheckForVictory) {
        this.type = type;
        this.nextPhases = nextPhases;
    }

    public String getType() {
        return type;
    }

    public List<Phase> getNextPhases() {
        return nextPhases;
    }

    public boolean getNeedCheckForVictory() {
        return needCheckForVictory;
    }

    /*
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;

        Phase phase = (Phase) o;
        if (phase.getType().equals(this.type)) return true;

        return false;
    }

     */
}
