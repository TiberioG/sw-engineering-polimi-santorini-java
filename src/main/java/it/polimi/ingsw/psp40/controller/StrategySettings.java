package it.polimi.ingsw.psp40.controller;

/**
 * This class represents an object with all strategy
 * @author Vito96
 */
public class StrategySettings {
    /* Attributes */
    private String strategyMove;
    private String StrategyWin;
    private String strategyBuild;

    /* Methods */

    public String getStrategyMove() {
        return strategyMove;
    }

    public void setStrategyMove(String strategyMove) {
        this.strategyMove = strategyMove;
    }

    public String getStrategyWin() {
        return StrategyWin;
    }

    public void setStrategyWin(String strategyWin) {
        StrategyWin = strategyWin;
    }

    public String getStrategyBuild() {
        return strategyBuild;
    }

    public void setStrategyBuild(String strategyBuild) {
        this.strategyBuild = strategyBuild;
    }
}
