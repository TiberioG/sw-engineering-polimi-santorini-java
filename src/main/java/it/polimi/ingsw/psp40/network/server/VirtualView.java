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

    /* todo teoricamente potrei usare match.getMatchID(), ma:
        1) forse match dobbiamo toglierlo da qua
        2) il controller istanzia il match e dopo fa virtualview.setMatch(match).
           Il problema è che il match quando viene istanziato fa in automatico un publish e quindi displayMessage() qua dentro
           quando prova ad accedere a match lo trova null perchè virtualview.setMatch(match) non è ancora stato fatto
           Possibile soluzione se non si vuole usare questa variabile: fare che il messaggo mandao dal match con il publish abbia anche
           il matchID settato e quindi dentro a displayMessage() fare un controllo
    */
    private int matchID;

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
        switch (message.getTypeOfMessage()) {
            case SETTED_CARDS_TO_GAME: //mando a far vedere le carte selezionate
                List<Card> cardInGame = match.getCards();
                displayMessage(new Message("ALL", TypeOfMessage.SETTED_CARDS_TO_GAME, cardInGame ));
                break;

            default:
                displayMessage(message);
                break;

        }
    }

    /**
     * Sends message to the client
     * @param message {@link Message} to be sent
     */
    public void displayMessage(Message message) {
        //message.setMatchID(match.getMatchID()); // vedi to-do sopra
        message.setMatchID(this.matchID);
        server.sendToClient(message);
    }

    /**
     * just a setter of match
     * @param match
     */
    public void setMatch(Match match){
        this.match = match;
    }

    public void setMatchID(int matchID) {
        this.matchID = matchID;
    }
}

