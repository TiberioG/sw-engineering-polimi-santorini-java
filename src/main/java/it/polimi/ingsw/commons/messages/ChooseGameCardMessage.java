package it.polimi.ingsw.commons.messages;

import it.polimi.ingsw.model.Card;

import java.util.HashMap;

public class ChooseGameCardMessage {
    private HashMap<Integer, Card> cardMap = null;
    private Integer numberOfPlayer;

    public ChooseGameCardMessage(HashMap<Integer, Card> cardMap, Integer numberOfPlayer) {
        this.cardMap = cardMap;
        this.numberOfPlayer = numberOfPlayer;
    }

    public HashMap<Integer, Card> getCardMap() {
        return cardMap;
    }

    public Integer getNumberOfPlayer() {
        return numberOfPlayer;
    }
}
