package it.polimi.ingsw.psp40.controller;

import it.polimi.ingsw.psp40.commons.PhaseType;

import java.util.List;

public class Phase {
    private PhaseType type;
    private List<Phase> nextPhases;
    private boolean needCheckForVictory;

    public Phase(PhaseType type, List<Phase> nextPhases, boolean needCheckForVictory) {
        this.type = type;
        this.nextPhases = nextPhases;
        this.needCheckForVictory = needCheckForVictory;
    }

    public PhaseType getType() {
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
