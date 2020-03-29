package it.polimi.ingsw.model;

import it.polimi.ingsw.commons.Colors;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * This is the class for the Player
 * @author Vito96
 */
public class Player {

    /* Attributes */

    private final String name;
    private final Date birthday;
    private String currentCard;
    private List<Worker> workers = new ArrayList<>();

    /* Constructor(s) */

    /**
     * Constructor
     */

    public Player(String name, Date birthday) {
        this.name = name;
        this.birthday = birthday;
    }

    /* Methods */

    public String getName() {
        return name;
    }
    public Date getBirthday() {
        return birthday;
    }
    public String getCurrentCard() {
        return currentCard;
    }
    public void setCurrentCard(String currentCard) {
        this.currentCard = currentCard;
    }


    /**
     * Method that allows the creation and addition of a worker
     * @param color color of the worker
     * @return worker the created worker
     */

    public Worker addWorker(Colors color) {
        Worker worker = new Worker(color);
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