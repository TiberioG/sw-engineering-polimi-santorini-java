package it.polimi.ingsw.psp40.controller.strategies.strategyLose;

import it.polimi.ingsw.psp40.commons.Colors;
import it.polimi.ingsw.psp40.model.Component;
import it.polimi.ingsw.psp40.model.CardManager;
import it.polimi.ingsw.psp40.controller.strategies.strategyMove.DefaultMove;
import it.polimi.ingsw.psp40.controller.strategies.strategyMove.StrategyMove;
import it.polimi.ingsw.psp40.model.Cell;
import it.polimi.ingsw.psp40.model.Match;
import it.polimi.ingsw.psp40.model.Player;
import it.polimi.ingsw.psp40.model.Worker;
import org.junit.Before;
import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;

public class DefaultLoseTest {

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
    StrategyLose strategyLose;

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

        initCellWorker1_1 = match.getIsland().getCell(3, 3);
        initCellWorker1_2 = match.getIsland().getCell(2, 3);
        initCellWorker2_1 = match.getIsland().getCell(0, 0);
        initCellWorker2_2 = match.getIsland().getCell(1, 0);
        match.getLocation().setLocation(initCellWorker1_1, worker1_1);
        match.getLocation().setLocation(initCellWorker1_2, worker1_2);
        match.getLocation().setLocation(initCellWorker2_1, worker2_1);
        match.getLocation().setLocation(initCellWorker2_2, worker2_2);

        match.getIsland().addComponent(Component.FIRST_LEVEL , match.getIsland().getCell(0, 1));
        match.getIsland().addComponent(Component.SECOND_LEVEL, match.getIsland().getCell(0, 1));

        match.getIsland().addComponent(Component.FIRST_LEVEL , match.getIsland().getCell(1, 1));
        match.getIsland().addComponent(Component.SECOND_LEVEL, match.getIsland().getCell(1, 1));

        match.getIsland().addComponent(Component.FIRST_LEVEL , match.getIsland().getCell(1, 2));
        match.getIsland().addComponent(Component.SECOND_LEVEL, match.getIsland().getCell(1, 2));

        match.getIsland().addComponent(Component.FIRST_LEVEL , match.getIsland().getCell(2, 1));
        match.getIsland().addComponent(Component.SECOND_LEVEL, match.getIsland().getCell(2, 1));

        match.getIsland().addComponent(Component.FIRST_LEVEL , match.getIsland().getCell(2, 0));
        match.getIsland().addComponent(Component.SECOND_LEVEL, match.getIsland().getCell(2, 0));
        match.getIsland().addComponent(Component.THIRD_LEVEL , match.getIsland().getCell(2, 0));

        strategyMove1 = new DefaultMove(match);
        strategyLose = new DefaultLose(match);
    }

    @Test
    public void checkLoseForMove_true_defaultMove() {
        match.setCurrentPlayer(player2);
        assertTrue(strategyLose.checkLoseForMove(strategyMove1));
    }

}
