package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.PlayerNotPresentException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.*;

public class MatchTest {
    /* declarations */
    private final int testID = 478939;
    private final String name1 = "Agrippina";
    private final String name2 = "Basilina";
    private final String name3 = "Cornelia";
    private final Date testDate1 = new Date();
    private final Date testDate2 = new Date(122333333);
    private final Date testDate3 = new Date(666666666);
    private Player player1;
    private Player player2;
    private Player player3;
    private Player playerNotAdded = new Player("Socrate", new Date(2393939));
    private Match testMatch;

    /**
     * Before starting the test I create the match and add the players
     */
    @Before
    public void initMatch() {
        testMatch = new Match(testID);
        player1 = this.testMatch.createPlayer(name1, testDate1);
        player2 = this.testMatch.createPlayer(name2, testDate2);
        player3 = this.testMatch.createPlayer(name3, testDate3);
    }

    /**
     * The match must return exactly the same id used when it was created by constructor
     */
    @Test
    public void testGetMatchID() {
        assertEquals(testID, testMatch.getMatchID());
    }

    /**
     * The match must return a non empty list of players
     * also the list returned contains exactly the same players added
     */
    @Test
    public void testGetPlayers() {
        //first create a list of players
        List<Player> testList = new ArrayList<>();
        testList.add(player1);
        testList.add(player2);
        testList.add(player3);

        assertNotNull(testMatch.getPlayers());
        assertEquals(testList, testMatch.getPlayers());
    }

    /**
     * Test shows that exists an island after creating a match
     */
    @Test
    public void testGetIsland() {
        assertNotNull(testMatch.getIsland());
    }

    /**
     * Test shows that the current player is exactly the one set
     */
    @Test
    public void testGetCurrentPlayer() {
        try {
            testMatch.setCurrentPlayer(player2);
        } catch (PlayerNotPresentException except) {
            //no need to catch it here
        }
        assertEquals(player2, testMatch.getCurrentPlayer());
    }

    /**
     * Test shows that if i try to set as current player one not preset in the list I get an exception
     */
    @Test(expected = PlayerNotPresentException.class)
    public void testGetCurrentPlayer_ExceptionPlayerNotPres() throws PlayerNotPresentException {
        testMatch.setCurrentPlayer(playerNotAdded);
    }

    /**
     * Test shows I get exactly the same list of cards as input
     */
    @Test
    public void testGetCards() {

        testMatch.addCard("Athena");
        testMatch.addCard("Demetra");
        testMatch.addCard("Zeus");

        assertEquals("Athena", testMatch.getCards().get(0));
        assertEquals("Demetra", testMatch.getCards().get(1));
        assertEquals("Zeus", testMatch.getCards().get(2));
    }

    /**
     * Test shows that after adding a card i get a non empty list
     */
    @Test
    public void addCard() {
        testMatch.addCard("Athena");
        assertNotNull(testMatch.getCards());
    }


    @Test
    public void testSetCurrentPlayer() {
        try {
            testMatch.setCurrentPlayer(player2);
        } catch (PlayerNotPresentException except) {
            //no need to catch it here
        }
        assertNotNull(testMatch.getCurrentPlayer());

    }

    @Test
    public void testGetLocation(){

    }

}

