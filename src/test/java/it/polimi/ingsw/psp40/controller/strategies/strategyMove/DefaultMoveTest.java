package it.polimi.ingsw.psp40.controller.strategies.strategyMove;

import it.polimi.ingsw.psp40.commons.Colors;
import it.polimi.ingsw.psp40.commons.Component;
import it.polimi.ingsw.psp40.exceptions.SantoriniException;
import it.polimi.ingsw.psp40.exceptions.WrongCellSelectedMoveException;
import it.polimi.ingsw.psp40.exceptions.ZeroCellsAvailableMoveException;
import it.polimi.ingsw.psp40.model.Cell;
import it.polimi.ingsw.psp40.model.Match;
import it.polimi.ingsw.psp40.model.Player;
import it.polimi.ingsw.psp40.model.Worker;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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

public class DefaultMoveTest {

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
    StrategyMove strategyMove;

    @Before
    public void setUp() throws Exception {
        match = new Match(12123131);

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String birthDate1 = "22/03/1998";
        String birthDate2 = "26/07/1997";
        Date date1 = dateFormat.parse(birthDate1);
        Date date2 = dateFormat.parse(birthDate2);
        player1 = match.createPlayer("Mario", date1);
        player2 = match.createPlayer("Luigi", date2);

        worker1_1 = player1.addWorker(Colors.RED);
        worker1_2 = player1.addWorker(Colors.RED);
        worker2_1 = player2.addWorker(Colors.BLUE);
        worker2_2 = player2.addWorker(Colors.BLUE);

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

        strategyMove = new DefaultMove(match);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void move() throws SantoriniException {
        Cell cellToMove1_1 = match.getIsland().getCell(2, 3);
        strategyMove.move(worker1_1, cellToMove1_1);
        assertEquals(worker1_1, match.getLocation().getOccupant(cellToMove1_1));
        assertNull(match.getLocation().getOccupant(initCellWorker1_1));

        Cell cellToMove2_2 = match.getIsland().getCell(2, 2);
        strategyMove.move(worker2_2, cellToMove2_2);
        assertEquals(worker2_2, match.getLocation().getOccupant(cellToMove2_2));
        assertNull(match.getLocation().getOccupant(initCellWorker2_2));
    }

    @Test
    public void move_levelUp() throws SantoriniException {
        Cell cellToMove1_1 = match.getIsland().getCell(1, 2);
        strategyMove.move(worker1_1, cellToMove1_1);
        assertEquals(worker1_1, match.getLocation().getOccupant(cellToMove1_1));
        assertNull(match.getLocation().getOccupant(initCellWorker1_1));
    }

    @Test
    public void move_moveSameLevel() throws SantoriniException {

        // add a level in that cell
        match.getIsland().addComponent(Component.FIRST_LEVEL, match.getIsland().getCell(0, 2));

        // here we go from level 0 to level 1
        Cell cellToMove1_1_firstMove = match.getIsland().getCell(1, 2);
        strategyMove.move(worker1_1, cellToMove1_1_firstMove);
        assertEquals(worker1_1, match.getLocation().getOccupant(cellToMove1_1_firstMove));
        assertNull(match.getLocation().getOccupant(initCellWorker1_1));

        // here we go from level 1 to level 1
        Cell cellToMove1_1_secondMove = match.getIsland().getCell(0, 2);
        strategyMove.move(worker1_1, cellToMove1_1_secondMove);
        assertEquals(worker1_1, match.getLocation().getOccupant(cellToMove1_1_secondMove));
        assertNull(match.getLocation().getOccupant(cellToMove1_1_firstMove));
    }

    @Test(expected = ZeroCellsAvailableMoveException.class)
    public void move_workerCanNotMove_throwsZeroCellsAvailableMoveException() throws SantoriniException {
        Cell cellToMove2_1 = match.getIsland().getCell(1, 1);
        strategyMove.move(worker2_1, cellToMove2_1);
    }

    @Test(expected = WrongCellSelectedMoveException.class)
    public void move_workerWantsToGoTooFar_throwsWrongCellSelectedMoveException() throws SantoriniException {
        Cell cellToMove1_1 = match.getIsland().getCell(2, 4);
        strategyMove.move(worker1_1, cellToMove1_1);
    }

    @Test(expected = WrongCellSelectedMoveException.class)
    public void move_workerWantsToReplaceAnotherWorker_throwsWrongCellSelectedMoveException() throws SantoriniException {
        Cell cellToMove1_1 = match.getIsland().getCell(3, 3);
        strategyMove.move(worker1_1, cellToMove1_1);
    }

    @Ignore
    @Test
    public void thisIsNotATest_justPrintAvailableCells() {
        String newLine = System.getProperty("line.separator");
        int indexPlayer = 1;
        for (Player player : match.getPlayers()) {
            int indexWorker = 1;
            for (Worker worker : player.getWorkers()) {
                System.out.println(newLine + "Worker" + indexPlayer + "_" + indexWorker++);
                List<Cell> availableCells = strategyMove.getAvailableCells(worker);
                if (availableCells.size() == 0) {
                    System.out.println("This worker can not move :(");
                }
                for (Cell cell : availableCells) {
                    System.out.println("x: " + cell.getCoordX() + ", y: " + cell.getCoordY());
                }
            }
            indexPlayer++;
        }
    }
}
