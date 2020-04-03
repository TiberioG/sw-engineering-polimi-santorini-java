package it.polimi.ingsw.controller.strategies.strategyBuild;

import it.polimi.ingsw.commons.Component;
import it.polimi.ingsw.exceptions.BuildLowerComponentException;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.model.Worker;

import java.util.ArrayList;

// EFESTO

public class DoubleComponent implements StrategyBuild {
    @Override
    public void build(ArrayList<Component> listCompToBuild, ArrayList<Cell> listWhereToBuild, Match match, Worker worker) {
        Cell whereIam = match.getLocation().getLocation(worker);
        ArrayList nearby = match.getIsland().getAdjCells(whereIam);


        //0check, since double build is allowed, size of list to build can be up to 2
        boolean cond0 = (listCompToBuild.size() <= 2 && listWhereToBuild.size() <= 2);

        //TODO verificare per prima cosa se mi sto avvalendo di doppia costruzione sennò basta fare check singoli
        //1st check, I should only build close to me
        boolean cond1 = nearby.contains(listWhereToBuild.get(0));
        boolean cond11 = nearby.contains(listWhereToBuild.get(1));

        //2nd check, where I wanna build there's not another worker
        boolean cond2 = (match.getLocation().getOccupant(listWhereToBuild.get(0)) == null);
        boolean cond22 = (match.getLocation().getOccupant(listWhereToBuild.get(1)) == null);

        //3rd check, what I wanna build is compatible with the level already present
        boolean cond3 = listWhereToBuild.get(0).getTower().nextBuildable() == listCompToBuild.get(0).getComponentCode();
        boolean cond33 = listWhereToBuild.get(1).getTower().nextBuildable() == listCompToBuild.get(1).getComponentCode();

        //4th check, second item to build not a dome
        boolean cond4 = listCompToBuild.get(1).getComponentCode() != 4;

        //5th check, second component to build must be in same location as first
        boolean cond5 = listWhereToBuild.get(1).equals(listWhereToBuild.get(0));


        //TODO gestire le eccezioni per ogni condizione

        if (cond0 && cond1 && cond11 && cond2 && cond22 && cond3 && cond33 && cond4 && cond5) {
            try {
                listWhereToBuild.get(0).getTower().addComponent(listCompToBuild.get(0));
            } catch (BuildLowerComponentException e) {
                e.printStackTrace();
            }
            try {
                listWhereToBuild.get(1).getTower().addComponent(listCompToBuild.get(1));
            } catch (BuildLowerComponentException e) {
                e.printStackTrace();
            }
        }
    }
}
