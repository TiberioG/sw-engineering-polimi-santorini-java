package it.polimi.ingsw.psp40.network.server;

import it.polimi.ingsw.psp40.commons.Listener;
import it.polimi.ingsw.psp40.commons.messages.Message;
import it.polimi.ingsw.psp40.commons.Publisher;
import it.polimi.ingsw.psp40.commons.messages.TypeOfMessage;
import it.polimi.ingsw.psp40.controller.Controller;
import it.polimi.ingsw.psp40.model.*;

import java.util.List;


//todo associazione player - client con .getconnection


/**
 * it's both publisher ->controller and Listener <- Model
 */
public class VirtualView extends Publisher<Message> implements Listener<Message> {

    private Match match;
    private CardManager cardManager;
    private Server server;

    /**
     * Constructor
     * @param server
     */
    public VirtualView( Server server){
        this.server = server;
        Controller controller = new Controller(this);
        addListener(controller);
    }

    public void handleMessage(Message message){
        publish(message); //just send a message to controller to create the match;
    }


    /**
     * Receives match changes
     * @param message
     */
    @Override
    public void update(Message message) {
        switch (message.getTypeOfMessage()){
            case TOWER_UPDATED:
                Cell[][] island = match.getIsland().getField();
                displayMessage(new Message("ALL", TypeOfMessage.TOWER_UPDATED, island));

            case SETTED_CARDS_TO_GAME: //mando a far vedere le carte selezionate
                List<Card> cardInGame = match.getCards();
                displayMessage(new Message("ALL", TypeOfMessage.SETTED_CARDS_TO_GAME, cardInGame ));
        }

        if (message.getUsername() != null && message.getUsername().equals("VIRTUAL_VIEW")) {

        } else displayMessage(message);
    }

    /**
     * Sends message to the client
     * @param message
     */
    public void displayMessage(Message message) {
        server.sendToClient(message);
    }

    /**
     * just a setter of match
     * @param match
     */
    public void setMatch(Match match){
        this.match = match;
    }
}

