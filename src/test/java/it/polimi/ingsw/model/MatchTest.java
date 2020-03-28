package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.PlayerNotPresentException;
import org.junit.Test;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class MatchTest {
    /**
     * test shhoes getting the matchID
     */
    @Test
    public void testGetMatchID() {
        final int testID = 6666666;
        Match testMatch = new Match(testID);

        assertEquals(testID, testMatch.getMatchID());
    }

    /**
     * test shows, after adding a player, thee match returns a non empty list of players
     * also the list returned contains exactly the same players added
     */
    @Test
    public void testGetPlayers() {
        final int testID = 478939;
        final String name1 = "Vesta";
        final String name2 = "Agrippina";
        final Date testDate1 = new Date();
        final Date testDate2 = new Date(122333333);

        Player player1= new Player(name1,testDate1);
        Player player2= new Player(name2,testDate2);
        List<Player> testList = new ArrayList<>();
        testList.add(player1);
        testList.add(player2);
        Match testMatch = new Match(testID);
        testMatch.addPlayer(player1);
        testMatch.addPlayer(player2);

        assertNotNull(testMatch.getPlayers());
        assertEquals(testList, testMatch.getPlayers());

    }

    /**
     * Test shows that exists an island after creating a match
     */
    @Test
    public void testGetIsland() {
        final int testID = 6666666;
        Match testMatch = new Match(testID);

        assertNotNull(testMatch.getIsland());
    }

    @Test
    public void testGetCurrentPlayer()  {
        final int testID = 478939;
        final String name1 = "Vesta";
        final String name2 = "Agrippina";
        final Date testDate1 = new Date();
        final Date testDate2 = new Date(122333333);

        Player player1= new Player(name1,testDate1);
        Player player2= new Player(name2,testDate2);
        Match testMatch = new Match(testID);

        testMatch.addPlayer(player1);
        testMatch.addPlayer(player2);

        try {
            testMatch.setCurrentPlayer(player2);
        }
        catch(PlayerNotPresentException except){
            /// boooh non so come gestirla nel test
        }
        assertEquals(player2, testMatch.getCurrentPlayer());
    }

    @Test (expected = PlayerNotPresentException.class)
    public void testGetCurrentPlayer2() throws PlayerNotPresentException {
        final int testID = 478939;
        final String name1 = "Vesta";
        final String name2 = "Agrippina";
        final String name3 = "Drusilla";
        final Date testDate1 = new Date();
        final Date testDate2 = new Date(122333333);
        final Date testDate3 = new Date(666666666);

        Player player1= new Player(name1,testDate1);
        Player player2= new Player(name2,testDate2);
        Player playerNotadded= new Player(name3,testDate3);

        Match testMatch = new Match(testID);

        testMatch.addPlayer(player1);
        testMatch.addPlayer(player2);

        testMatch.setCurrentPlayer(playerNotadded);


    }

    @Test
    public void testGetCards() {
    }

    @Test
    public void addCard() {
    }

    @Test
    public void testSetCurrentPlayer() {

    }

    @Test
    public void testCreatePlayer() {
    }
}
