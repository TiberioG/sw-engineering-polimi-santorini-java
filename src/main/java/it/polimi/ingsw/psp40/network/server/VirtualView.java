package it.polimi.ingsw.psp40.network.server;

import it.polimi.ingsw.psp40.commons.Listener;
import it.polimi.ingsw.psp40.commons.Publisher;
import it.polimi.ingsw.psp40.commons.messages.Message;
import it.polimi.ingsw.psp40.controller.Controller;

import java.util.List;

/**
 * it's both publisher ->controller and Listener <- Model
 */
public class VirtualView extends Publisher<Message> implements Listener<Message> {

    private Server server;

    private int matchID;

    /**
     * Constructor
     *
     * @param server
     */
    public VirtualView(Server server) {
        this.server = server;
        Controller controller = new Controller(this);
        addListener(controller);
    }

    /**
     * Handles a received message
     *
     * @param message message received
     */
    public void handleMessage(Message message) {
        publish(message); //just send a message to controller to create the match;
    }

    /**
     * Receives match changes
     *
     * @param message
     */
    @Override
    public void update(Message message) {
        displayMessage(message);
    }

    /**
     * Sends message to the client
     *
     * @param message {@link Message} to be sent
     */
    public void displayMessage(Message message) {
        message.setMatchID(this.matchID);
        server.sendToClient(message);
    }

    /**
     * Setter of matchID
     *
     * @param matchID
     */
    public void setMatchID(int matchID) {
        this.matchID = matchID;
    }

    /**
     * Restores a broken match
     *
     * @param usernames list of players' usernames
     */
    public void restoreMatch(List<String> usernames) {
        server.restoreMatch(matchID, this, usernames);
    }

    /**
     * Remove a user from the match
     *
     * @param username username of the user to be removed
     */
    public void disconnectUser(String username) {
        server.removeUserSilently(username);
    }
}

