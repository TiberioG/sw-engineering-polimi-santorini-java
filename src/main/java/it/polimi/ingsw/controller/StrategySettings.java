package it.polimi.ingsw.controller;

public class StrategySettings {
    private String strategyMove;
    private String StrategyWin;
    private String strategyBuild;

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
