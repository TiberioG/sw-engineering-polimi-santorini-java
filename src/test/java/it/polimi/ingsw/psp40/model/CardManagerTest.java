package it.polimi.ingsw.psp40.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class CardManagerTest {

    @Test
    public void retrieveSingletonClass_receiveSameInstance() {
        CardManager firstInstance = CardManager.initCardManager();
        CardManager secondInstance = CardManager.initCardManager();
        assertSame(firstInstance, secondInstance);
    }

    @Test
    public void getCardById_negativeId_retrieveNull() {
        CardManager firstInstance = CardManager.initCardManager();
        Card card = firstInstance.getCardById(-1);
        assertNull(card);
    }

    @Test
    public void getCardMap_namesCardAreCorrect(){
        CardManager firstInstance = CardManager.initCardManager();
        assertEquals("Apollo", firstInstance.getCardMap().get(0).getName());
    }
}
