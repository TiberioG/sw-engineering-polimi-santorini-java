package it.polimi.ingsw.psp40.model;

import it.polimi.ingsw.psp40.commons.Colors;
import it.polimi.ingsw.psp40.commons.messages.Message;
import it.polimi.ingsw.psp40.commons.Publisher;
import it.polimi.ingsw.psp40.commons.messages.TypeOfMessage;
import it.polimi.ingsw.psp40.network.server.VirtualView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * This is the class for the Player
 * @author Vito96
 */
public class Player extends Publisher<Message> {

    /* Attributes */

    private final String name;
    private final Date birthday;
    private Card currentCard;
    private List<Worker> workers = new ArrayList<>();
    //private String UUID;

    /* Constructor(s) */

    /**
     * Constructor
     */
    public Player(String name, Date birthday, VirtualView virtualView) {
        addListener(virtualView);
        this.name = name;
        this.birthday = birthday;
    }

    // Constructor for testing
    public Player(String name, Date birthday) {
        this(name, birthday, null);
    }

    /* Methods */

    public String getName() {
        return name;
    }

    public Date getBirthday() {
        return birthday;
    }

    public Card getCurrentCard() {
        return currentCard;
    }

    public void setCurrentCard(Card currentCard) {
        this.currentCard = currentCard;
        publish(new Message("ALL", TypeOfMessage.PLAYER_UPDATED, this));
    }

    /**
     * Method that allows the creation and addition of a worker
     * @param color color of the worker
     * @return worker the created worker
     */
    public Worker addWorker(Colors color) {
        int id = 0;
        if (workers.size() > 0) id = workers.get(workers.size() - 1).getId() + 1;
        Worker worker = new Worker(id, color, this);
        workers.add(worker);
        publish(new Message("ALL", TypeOfMessage.PLAYER_UPDATED, this));
        return worker;
    }

    /**
     * @return an ArrayList of the workers belonging to the player
     */
    public List<Worker> getWorkers() {
        return workers;
    }
}
