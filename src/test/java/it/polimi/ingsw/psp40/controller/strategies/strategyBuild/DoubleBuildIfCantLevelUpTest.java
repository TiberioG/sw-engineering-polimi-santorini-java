package it.polimi.ingsw.psp40.controller.strategies.strategyBuild;

import it.polimi.ingsw.psp40.commons.Colors;
import it.polimi.ingsw.psp40.model.Component;
import it.polimi.ingsw.psp40.commons.PhaseType;
import it.polimi.ingsw.psp40.exceptions.ComponentNotAllowed;
import it.polimi.ingsw.psp40.exceptions.SantoriniException;
import it.polimi.ingsw.psp40.exceptions.WrongCellSelectedBuildException;
import it.polimi.ingsw.psp40.exceptions.ZeroCellsAvailableBuildException;
import it.polimi.ingsw.psp40.model.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

import static org.junit.Assert.*;

/*
Initial Map:
╔═════╦═══════════╤═══════════╤═══════════╤═══════════╤═══════════╗
║  4  ║           │           │           │           │           ║
╟─────╫───────────┼───────────┼───────────┼───────────┼───────────╢
║  3  ║           │           │           │ Worker1_2 │           ║
╟─────╫───────────┼───────────┼───────────┼───────────┼───────────╢
║  2  ║           │   LVL1    │ Worker1_1 │           │           ║
╟─────╫───────────┼───────────┼───────────┼───────────┼───────────╢
║  1  ║   LVL2    │ Worker2_2 │           │           │           ║
╟─────╫───────────┼───────────┼───────────┼───────────┼───────────╢
║  0  ║ Worker2_1 │   LVL2    │           │           │           ║
╠═════╬═══════════╪═══════════╪═══════════╪═══════════╪═══════════╣
║ y/x ║     0     │     1     │     2     │     3     │     4     ║
╚═════╩═══════════╧═══════════╧═══════════╧═══════════╧═══════════╝
 */

public class DoubleBuildIfCantLevelUpTest {
    private Match match;
    private Player player1;
    private Player player2;
    Worker worker1_1;
    Worker worker1_2;
    Worker worker2_1;
    Worker worker2_2;
    Cell initCellWorker1_1;
    Cell initCellWorker1_2;
    Cell initCellWorker2_1;
    Cell initCellWorker2_2;
    StrategyBuild strategyBuild;

    @Before
    public void setUp() throws Exception {

        match = new Match(66666);

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String birthDate1 = "22/03/1998";
        String birthDate2 = "26/07/1997";
        Date date1 = dateFormat.parse(birthDate1);
        Date date2 = dateFormat.parse(birthDate2);
        player1 = match.createPlayer("Mariossssss", date1);
        player2 = match.createPlayer("Luigi", date2);

        worker1_1 = player1.addWorker(Colors.RED);
        worker1_2 = player1.addWorker(Colors.YELLOW);
        worker2_1 = player2.addWorker(Colors.BLUE);
        worker2_2 = player2.addWorker(Colors.GREEN);

        initCellWorker1_1 = match.getIsland().getCell(2, 2);
        initCellWorker1_2 = match.getIsland().getCell(3, 3);
        initCellWorker2_1 = match.getIsland().getCell(0, 0);
        initCellWorker2_2 = match.getIsland().getCell(1, 1);
        match.getLocation().setLocation(initCellWorker1_1, worker1_1);
        match.getLocation().setLocation(initCellWorker1_2, worker1_2);
        match.getLocation().setLocation(initCellWorker2_1, worker2_1);
        match.getLocation().setLocation(initCellWorker2_2, worker2_2);

        match.getIsland().addComponent(Component.FIRST_LEVEL, match.getIsland().getCell(0, 0));
        match.getIsland().addComponent(Component.SECOND_LEVEL, match.getIsland().getCell(0, 0));
        match.getIsland().addComponent(Component.THIRD_LEVEL, match.getIsland().getCell(0, 0));

        match.getIsland().addComponent(Component.FIRST_LEVEL, match.getIsland().getCell(0, 1));
        match.getIsland().addComponent(Component.SECOND_LEVEL, match.getIsland().getCell(0, 1));

        match.getIsland().addComponent(Component.FIRST_LEVEL, match.getIsland().getCell(1, 0));
        match.getIsland().addComponent(Component.SECOND_LEVEL, match.getIsland().getCell(1, 0));

        match.getIsland().addComponent(Component.FIRST_LEVEL, match.getIsland().getCell(1, 2));

        strategyBuild = new DoubleBuildIfCantLevelUp(match);
        match.getMatchProperties().resetParameterForTurn();
        match.getMatchProperties().getPerformedPhases().add(PhaseType.MOVE_WORKER);
    }

    @Test
    public void build_beforeMove_shouldReturnSomeAvailableCells() throws SantoriniException {
        match.getMatchProperties().getPerformedPhases().clear(); // remove the move phase
        List<Cell> buildableCells = strategyBuild.getBuildableCells(worker2_1);
        assertEquals(2, buildableCells.size());
        assertTrue(buildableCells.contains(match.getIsland().getCell(0,1)));
        assertTrue(buildableCells.contains(match.getIsland().getCell(1,0)));
    }

    @Test
    public void build_beforeMove_shouldReturnEmptyAvailableCells() throws SantoriniException {
        match.getIsland().removeComponent(match.getIsland().getCell(0,0)); // reduce level: level 3 --> level 2
        match.getIsland().removeComponent(match.getIsland().getCell(0,0)); // reduce level: level 2 --> level 1
        match.getMatchProperties().getPerformedPhases().clear(); // remove the move phase
        assertEquals(0, strategyBuild.getBuildableCells(worker2_1).size());
    }

    @Test
    public void build_levelZero_shouldBeSuccessful() throws SantoriniException {
        Cell cellToBuild1_1 = match.getIsland().getCell(4, 4);
        strategyBuild.build(Component.FIRST_LEVEL, cellToBuild1_1, worker1_2);
        assertEquals(cellToBuild1_1.getTower().getTopComponent(), Component.FIRST_LEVEL);
    }

    @Test(expected = WrongCellSelectedBuildException.class)
    public void build_workerWantsToBuildTooFar_throwsWrongCellSelectedBuildException() throws SantoriniException {
        Cell cellToBuild1_1 = match.getIsland().getCell(3, 0);
        strategyBuild.build(Component.FIRST_LEVEL, cellToBuild1_1, worker1_2);
        assertNotEquals(cellToBuild1_1.getTower().getTopComponent(), Component.FIRST_LEVEL);
    }

    @Test(expected = WrongCellSelectedBuildException.class)
    public void build_workerWantsToBuildSameHisCell_throwsWrongCellSelectedBuildException() throws SantoriniException {
        strategyBuild.build(Component.FIRST_LEVEL, initCellWorker1_2, worker1_2);
        assertNotEquals(initCellWorker1_2.getTower().getTopComponent(), Component.FIRST_LEVEL);
    }

    @Test(expected = ComponentNotAllowed.class)
    public void build_workerWantsBuildInSky() throws SantoriniException {
        Cell cellToBuild2_2 = match.getIsland().getCell(1, 2);
        strategyBuild.build(Component.THIRD_LEVEL, cellToBuild2_2, worker2_2);
        assertNotEquals(initCellWorker1_2.getTower().getTopComponent(), Component.FIRST_LEVEL);
    }

    @Test(expected = ZeroCellsAvailableBuildException.class)
    public void build_workerBuriesHimself() throws SantoriniException {
        Cell cellToBuild1 = match.getIsland().getCell(0, 1);
        Cell cellToBuild2 = match.getIsland().getCell(1, 0);

        strategyBuild.build(Component.THIRD_LEVEL, cellToBuild1, worker2_1);
        strategyBuild.build(Component.THIRD_LEVEL, cellToBuild2, worker2_1);
        strategyBuild.build(Component.DOME, cellToBuild1, worker2_1);
        strategyBuild.build(Component.DOME, cellToBuild2, worker2_1);
        strategyBuild.build(Component.DOME, cellToBuild2, worker2_1);

    }

    @After
    public void showIsland() {

        Cell[][] field = match.getIsland().getField();
        Location location = match.getLocation();

        String[][] stringIsland = new String[field.length][field.length];

        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field.length; j++) {
                stringIsland[i][j] = "  LEV" + field[i][j].getTower().getTopComponent().getComponentCode() + " ";
                if (location.getOccupant(field[i][j]) != null) {
                    String owner = location.getOccupant(field[i][j]).getOwner().getName();
                    String trimOwner = owner.substring(0, Math.min(owner.length(), 3));
                    String workerCol = location.getOccupant(field[i][j]).getColor().getAnsiCode();
                    stringIsland[i][j] = Colors.reset() + workerCol + stringIsland[i][j] + trimOwner + Colors.reset() + "  ";
                } else {
                    stringIsland[i][j] = "  " + stringIsland[i][j] + "   ";
                }
            }
        }

        String lineSplit = "";
        StringJoiner splitJoiner = new StringJoiner("┼", "|", "|");
        for (int i= 0; i <5 ; i++) {
            splitJoiner.add(String.format("%14s", "").replace(" ", "-"));
        }
        lineSplit = splitJoiner.toString();
        for (String[] row : stringIsland) {
            StringJoiner sj = new StringJoiner(" | ", "| ", " |");
            for (String col : row) {
                sj.add(col);
            }
            System.out.println(lineSplit);
            System.out.println(sj.toString());
        }
        System.out.println(lineSplit);
        System.out.println(System.lineSeparator());

    }


}


