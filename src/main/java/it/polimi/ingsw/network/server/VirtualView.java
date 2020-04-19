package it.polimi.ingsw.network.server;

import it.polimi.ingsw.commons.Listener;
import it.polimi.ingsw.commons.messages.Message;
import it.polimi.ingsw.commons.Publisher;
import it.polimi.ingsw.commons.messages.TypeOfMessage;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.Match;


//todo associazione player - client con .getconnection


/**
 * it's both publisher ->controller and Listener <- Model
 */
public class VirtualView extends Publisher<Message> implements Listener<Message> {

    Match match;
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


    //metodo che o mi tengo i player prima che esistono
    //oppure inserisco appena arrivano

    /**
     *la chiama il Server
     *crea match
     */
    public void initGame(){
        publish(new Message("ALL", TypeOfMessage.START_MATCH, null)); //just send a message to controller to create the match
    }


    /**
     * Receives match changes
     * @param object
     */
    @Override
    public void update(Message object) {
        if (object.getUsername().equals("VIRTUAL_VIEW")) {
            
        } else displayMessage(object);
    }

    /**
     * Sends message to the client
     * @param message
     */
    public void displayMessage(Message message) {
        server.sendToClient(message);
    }

}

