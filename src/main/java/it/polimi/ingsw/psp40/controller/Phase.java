package it.polimi.ingsw.psp40.controller;

import it.polimi.ingsw.psp40.commons.PhaseType;

import java.util.List;

/**
 * This class rappresent a phase of {@link Turn}
 *
 * @author Vito96
 */
public class Phase {
    private PhaseType type;
    private List<Phase> nextPhases;
    private boolean needCheckForVictory;

    /**
     * Constructor
     *
     * @param type
     * @param nextPhases
     * @param needCheckForVictory
     */
    public Phase(PhaseType type, List<Phase> nextPhases, boolean needCheckForVictory) {
        this.type = type;
        this.nextPhases = nextPhases;
        this.needCheckForVictory = needCheckForVictory;
    }

    /**
     * Getter of the type of phase
     *
     * @return the type of the pahse
     */
    public PhaseType getType() {
        return type;
    }

    /**
     * Getter of the next phases available
     *
     * @return an list of the next phases
     */
    public List<Phase> getNextPhases() {
        return nextPhases;
    }

    /**
     * Getter for needCheckForVictory
     *
     * @return the boolean which indicate if the phase need a check for victory
     */
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
