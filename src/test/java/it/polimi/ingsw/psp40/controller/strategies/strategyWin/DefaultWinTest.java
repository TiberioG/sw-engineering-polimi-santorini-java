package it.polimi.ingsw.psp40.controller.strategies.strategyWin;

import it.polimi.ingsw.psp40.commons.Colors;
import it.polimi.ingsw.psp40.commons.Component;
import it.polimi.ingsw.psp40.model.CardManager;
import it.polimi.ingsw.psp40.controller.TurnManager;
import it.polimi.ingsw.psp40.controller.strategies.strategyMove.DefaultMove;
import it.polimi.ingsw.psp40.controller.strategies.strategyMove.PushEnemyWorker;
import it.polimi.ingsw.psp40.controller.strategies.strategyMove.StrategyMove;
import it.polimi.ingsw.psp40.exceptions.SantoriniException;
import it.polimi.ingsw.psp40.model.Cell;
import it.polimi.ingsw.psp40.model.Match;
import it.polimi.ingsw.psp40.model.Player;
import it.polimi.ingsw.psp40.model.Worker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;

/*
Initial Map:
╔═════╦═══════════╤═══════════╤═══════════╤═══════════╤═══════════╗
║  4  ║           │           │           │           │           ║
╟─────╫───────────┼───────────┼───────────┼───────────┼───────────╢
║  3  ║           │   LVL1    │ Worker1_1 │ Worker1_2 │           ║
║     ║           │           │           │  (LVL2)   │           ║
╟─────╫───────────┼───────────┼───────────┼───────────┼───────────╢
║  2  ║           │   LVL3    │ Worker2_2 │           │           ║
║     ║           │           │  (LVL2)   │           │           ║
╟─────╫───────────┼───────────┼───────────┼───────────┼───────────╢
║  1  ║   LVL2    │   LVL3    │           │           │           ║
╟─────╫───────────┼───────────┼───────────┼───────────┼───────────╢
║  0  ║ Worker2_1 │   LVL2    │           │           │           ║
╠═════╬═══════════╪═══════════╪═══════════╪═══════════╪═══════════╣
║ y/x ║     0     │     1     │     2     │     3     │     4     ║
╚═════╩═══════════╧═══════════╧═══════════╧═══════════╧═══════════╝
 */

public class DefaultWinTest {

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
    StrategyMove strategyMove1;
    StrategyMove strategyMove2;
    StrategyWin strategyWin;

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

        CardManager cardManager = CardManager.initCardManager();
        player1.setCurrentCard(cardManager.getCardById(0));
        player2.setCurrentCard(cardManager.getCardById(1));

        worker1_1 = player1.addWorker(Colors.RED);
        worker1_2 = player1.addWorker(Colors.RED);
        worker2_1 = player2.addWorker(Colors.BLUE);
        worker2_2 = player2.addWorker(Colors.BLUE);

        initCellWorker1_1 = match.getIsland().getCell(2, 3);
        initCellWorker1_2 = match.getIsland().getCell(3, 3);
        initCellWorker2_1 = match.getIsland().getCell(0, 0);
        initCellWorker2_2 = match.getIsland().getCell(2, 2);
        match.getLocation().setLocation(initCellWorker1_1, worker1_1);
        match.getLocation().setLocation(initCellWorker1_2, worker1_2);
        match.getLocation().setLocation(initCellWorker2_1, worker2_1);
        match.getLocation().setLocation(initCellWorker2_2, worker2_2);

        match.getIsland().addComponent(Component.FIRST_LEVEL , match.getIsland().getCell(0, 1));
        match.getIsland().addComponent(Component.SECOND_LEVEL, match.getIsland().getCell(0, 1));

        match.getIsland().addComponent(Component.FIRST_LEVEL , match.getIsland().getCell(1, 0));
        match.getIsland().addComponent(Component.SECOND_LEVEL, match.getIsland().getCell(1, 0));

        match.getIsland().addComponent(Component.FIRST_LEVEL , match.getIsland().getCell(1, 1));
        match.getIsland().addComponent(Component.SECOND_LEVEL, match.getIsland().getCell(1, 1));
        match.getIsland().addComponent(Component.THIRD_LEVEL , match.getIsland().getCell(1, 1));

        match.getIsland().addComponent(Component.FIRST_LEVEL , match.getIsland().getCell(1, 2));
        match.getIsland().addComponent(Component.SECOND_LEVEL, match.getIsland().getCell(1, 2));
        match.getIsland().addComponent(Component.THIRD_LEVEL , match.getIsland().getCell(1, 2));

        match.getIsland().addComponent(Component.FIRST_LEVEL , match.getIsland().getCell(2, 2));
        match.getIsland().addComponent(Component.SECOND_LEVEL, match.getIsland().getCell(2, 2));

        match.getIsland().addComponent(Component.FIRST_LEVEL , match.getIsland().getCell(3, 3));
        match.getIsland().addComponent(Component.SECOND_LEVEL, match.getIsland().getCell(3, 3));

        match.getIsland().addComponent(Component.FIRST_LEVEL , match.getIsland().getCell(1, 3));

        strategyWin = new DefaultWin(match);
        strategyMove1 = new DefaultMove(match);
        strategyMove2 = new PushEnemyWorker(match);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void checkWin_true_defaultMove() throws SantoriniException {
        match.setCurrentPlayer(player2);
        //TurnManager turnManager = new TurnManager(match);

        // go from LVL2 to LVL3
        Cell cellToMove2_2 = match.getIsland().getCell(1, 2);
        strategyMove1.move(worker2_2, cellToMove2_2);
        boolean win = strategyWin.checkWin();
        assertTrue(win);
    }

    @Test
    public void checkWin_false_defaultMove() throws SantoriniException {
        match.setCurrentPlayer(player1);
        TurnManager turnManager = new TurnManager(match);

        // go from LVL0 to LVL1
        Cell cellToMove1_1 = match.getIsland().getCell(1, 3);
        strategyMove1.move(worker1_1, cellToMove1_1);
        assertFalse(strategyWin.checkWin());
    }

    // To check if the enemy won because one of his has been pushed from LVL2 to LVL3 (should not win!)
    @Test
    public void checkWin_pushEnemyWorkerMove() throws SantoriniException {
        match.setCurrentPlayer(player1);
        TurnManager turnManager = new TurnManager(match);

        // go from LVL2 to LVL2 pushing an enemy worker. Enemy worker goes to LVL3 (and should not win!)
        Cell cellToMove1_2_first = initCellWorker2_2;
        strategyMove2.move(worker1_2, cellToMove1_2_first);
        assertFalse(strategyWin.checkWin());

        // go from LVL2 to LVL3
        Cell cellToMove1_2_second = match.getIsland().getCell(1, 2);
        strategyMove2.move(worker1_2, cellToMove1_2_second);
        assertTrue(strategyWin.checkWin());
    }
}
