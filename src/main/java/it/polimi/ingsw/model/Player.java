package it.polimi.ingsw.model;

import it.polimi.ingsw.commons.Colors;
import it.polimi.ingsw.commons.Publisher;
import it.polimi.ingsw.commons.VirtualViewEventName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;


/**
 * This is the class for the Player
 * @author Vito96
 */
public class Player extends Publisher<VirtualViewEventName> {

    /* Attributes */

    private final String name;
    private final Date birthday;
    private int currentCard;
    private List<Worker> workers = new ArrayList<>();

    /* Constructor(s) */

    /**
     * Constructor
     */

    protected Player(String name, Date birthday /* VirtualView view*/) {
        this.name = name;
        this.birthday = birthday;
        //addlistener(view);
    }

    /* Methods */

    public String getName() {
        return name;
    }
    public Date getBirthday() {
        return birthday;
    }
    public int getCurrentCard() {
        return currentCard;
    }
    public void setCurrentCard(int currentCard) {
        this.currentCard = currentCard;
    }


    /**
     * Method that allows the creation and addition of a worker
     * @param color color of the worker
     * @return worker the created worker
     */

    public Worker addWorker(Colors color) {
        Worker worker = new Worker(color, this);
        workers.add(worker);
        return worker;
    }

    /**
     * @return an ArrayList of the workers belonging to the player
     */
    public List<Worker> getWorkers() {
        return workers;
    }
}