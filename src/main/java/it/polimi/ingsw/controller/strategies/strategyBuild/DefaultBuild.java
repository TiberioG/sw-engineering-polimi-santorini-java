package it.polimi.ingsw.controller.strategies.strategyBuild;

import it.polimi.ingsw.commons.Component;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.*;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultBuild implements StrategyBuild{

    public void buildComponent(Cell cell) {
        Tower currentTower = cell.getTower();
        try {
            currentTower.addComponent(component);
        } catch (BuildLowerComponentException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void build(ArrayList<Component> listCompToBuild, ArrayList<Cell> listWhereToBuild, Match match, Worker worker) {
        Cell whereIam = match.getLocation().getLocation(worker);
        ArrayList nearby = match.getIsland().getAdjCells(whereIam);


        //0check, since is default strategy i con only build once
        boolean cond0 = ( listCompToBuild.size() == 1 && listWhereToBuild.size() == 1 );

        //1st check, I should only build close to me
        boolean cond1 = nearby.contains(listWhereToBuild.get(0));

        //2nd check, where I wanna build there's not another worker
        boolean cond2 = (match.getLocation().getOccupant(listWhereToBuild.get(0)) == null);

        //3rd check, what I wanna build is compatible with the level already present
        boolean cond3 = listWhereToBuild.get(0).getTower().nextBuildable() == listCompToBuild.get(0).getComponentCode();

        //TODO gestire le eccezioni per ogni condizione

        if (cond0 && cond1 && cond2 && cond3){
            try {
                listWhereToBuild.get(0).getTower().addComponent(listCompToBuild.get(0));
            } catch (BuildLowerComponentException e) {
                e.printStackTrace();
            }

        }







    }
}
