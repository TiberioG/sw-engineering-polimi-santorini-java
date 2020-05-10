package it.polimi.ingsw.psp40.controller;

/**
 * This class represents an object with all strategy
 * @author Vito96
 */
public class StrategySettings {
    /* Attributes */
    private String strategyMove;
    private String strategyWin;
    private String strategyBuild;
    private String strategyLose;

    /* Methods */

    public String getStrategyMove() {
        return strategyMove;
    }

    public void setStrategyMove(String strategyMove) {
        this.strategyMove = strategyMove;
    }

    public String getStrategyWin() {
        return strategyWin;
    }

    public void setStrategyWin(String strategyWin) {
        strategyWin = strategyWin;
    }

    public String getStrategyBuild() {
        return strategyBuild;
    }

    public void setStrategyBuild(String strategyBuild) {
        this.strategyBuild = strategyBuild;
    }

    public String getStrategyLose() {
        return strategyLose;
    }

    public void setStrategyLose(String strategyLose) {
        strategyLose = strategyLose;
    }
}
