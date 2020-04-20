package it.polimi.ingsw.network.server;

import it.polimi.ingsw.commons.Listener;
import it.polimi.ingsw.commons.messages.Message;
import it.polimi.ingsw.commons.Publisher;
import it.polimi.ingsw.commons.messages.TypeOfMessage;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.CardManager;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Location;
import it.polimi.ingsw.model.Match;

import java.util.Calendar;
import java.util.List;


//todo associazione player - client con .getconnection


/**
 * it's both publisher ->controller and Listener <- Model
 */
public class VirtualView extends Publisher<Message> implements Listener<Message> {

    Match match;
    CardManager cardManager;
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

    /**
     *la chiama il Server
     *crea match
     */
    public void initGame(List<String> matchUser){
        publish(new Message("ALL", TypeOfMessage.START_MATCH, matchUser)); //just send a message to controller to create the match;
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
                displayMessage(new Message("ALL",TypeOfMessage.TOWER_UPDATED, island));

            case LOCATION_UPDATED:
                Location location = match.getLocation();
                displayMessage(new Message("ALL", TypeOfMessage.LOCATION_UPDATED, location));

            // todo è giusta questa logica? Che la virtualView riceva in questo modo un messaggio da server
            case CHOOSE_GAME_CARDS:
                // todo qua il payload di messaggio contiene le 2/3 carte scelte dal giocatore master. Andare a settare queste carte da qualche parte
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

