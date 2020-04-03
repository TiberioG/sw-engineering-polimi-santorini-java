package it.polimi.ingsw.controller.strategies.strategyBuild;

import it.polimi.ingsw.commons.Component;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.*;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultBuild implements StrategyBuild {

    private List<Cell> getAdjacentCells(Location location, Island island) {
        int x = worke.getCurrentCell().getCoordX();
        int y = worker.getCurrentCell().getCoordY();
        List<Cell> adjacentCells = new ArrayList<>();
        Cell[][] field =  island.getField();

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if ((i != 0) ^ ( j!= 0)) {
                    if (x+i >= 0 && y+j >= 0 && x+i<field[0].length && y+j<field.length) { //check boundaries
                        adjacentCells.add(field[x+i][y+j]);
                    }
                }
            }
        }


    }


    @Override
    public void build(Component component, Cell cell, Worker worker){
            Tower currentTower = cell.getTower();
            try {
                currentTower.addComponent(component);
            } catch (BuildLowerComponentException e) {
                e.printStackTrace();
            }

        }







   //strategy build

    public void buildComponent(Cell cell) {
        Tower currentTower = cell.getTower();
        try {
            currentTower.addComponent(component);
        } catch (BuildLowerComponentException e) {
            e.printStackTrace();
        }
    }




}
