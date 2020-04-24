package it.polimi.ingsw.controller.strategies.strategyBuild;

import it.polimi.ingsw.commons.Colors;
import it.polimi.ingsw.commons.Component;
import it.polimi.ingsw.controller.TurnProperties;
import it.polimi.ingsw.exceptions.SantoriniException;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.network.server.Server;
import it.polimi.ingsw.network.server.VirtualView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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

public class DefaultBuildTest {
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
    TurnProperties turnProperties;

    @Before
    public void setUp() throws Exception {
        match = new Match(66666, new VirtualView(new Server()));

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

        match.getIsland().addComponent(Component.FIRST_LEVEL, match.getIsland().getCell(0, 1));
        match.getIsland().addComponent(Component.SECOND_LEVEL, match.getIsland().getCell(0, 1));

        match.getIsland().addComponent(Component.FIRST_LEVEL, match.getIsland().getCell(1, 0));
        match.getIsland().addComponent(Component.SECOND_LEVEL, match.getIsland().getCell(1, 0));

        match.getIsland().addComponent(Component.FIRST_LEVEL, match.getIsland().getCell(1, 2));

        strategyBuild = new DefaultBuild(match);
        turnProperties = new TurnProperties();
        TurnProperties.resetAllParameter();
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

    }


}


