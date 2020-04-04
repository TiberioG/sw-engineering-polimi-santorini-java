package it.polimi.ingsw.controller.strategies.strategyBuild;

import it.polimi.ingsw.commons.Component;
import it.polimi.ingsw.exceptions.BuildLowerComponentException;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.model.Worker;

import java.util.ArrayList;

// ATLANTE

public class DomeAnywhere implements StrategyBuild {
    @Override
    public void build(ArrayList<Component> listCompToBuild, ArrayList<Cell> listWhereToBuild, Match match, Worker worker) {
        Cell whereIam = match.getLocation().getLocation(worker);
        ArrayList nearby = match.getIsland().getAdjCells(whereIam);

        //0check, atlante can only build once
        boolean cond0 = (listCompToBuild.size() == 1 && listWhereToBuild.size() == 1);

        //1st check, I should only build close to me
        boolean cond1 = nearby.contains(listWhereToBuild.get(0));

        //2nd check, where I wanna build there's not another worker
        boolean cond2 = (match.getLocation().getOccupant(listWhereToBuild.get(0)) == null);

        //no more check next thing buildable in list

        //TODO gestire le eccezioni per ogni condizione

        if (cond0 && cond1 && cond2) {
            try {
                listWhereToBuild.get(0).getTower().addComponent(listCompToBuild.get(0));
            } catch (BuildLowerComponentException e) {
                e.printStackTrace();
            }
        }
    }
}
