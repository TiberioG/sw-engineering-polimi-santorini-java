package it.polimi.ingsw.model;

import it.polimi.ingsw.commons.messages.Message;
import it.polimi.ingsw.commons.Publisher;
import it.polimi.ingsw.commons.messages.TypeOfMessage;
import it.polimi.ingsw.exceptions.WorkerAlreadyPresentException;
import it.polimi.ingsw.network.server.VirtualView;

import java.util.HashMap;

/**
 * This class represents the location
 * we want to represent here the 1:1 relationship between Worker and Cell
 */

public class Location extends Publisher<Message> {
    private HashMap<Cell, Worker> map = new HashMap<>();


    public Location(VirtualView virtualView) {
        addListener(virtualView);
    }

    public Location() {}


    /**
     * Method to add a pair cell-worker in the map
     * @param cell
     * @param worker
     * @throws WorkerAlreadyPresentException
     */
    public void setLocation(Cell cell, Worker worker) throws WorkerAlreadyPresentException {
        if (this.map.get(cell) != null) {
            throw new WorkerAlreadyPresentException();
        } else {
            this.map.put(getLocation(worker), null);
            this.map.put(cell, worker);

            this.update();  //every time i change the location I send a copy of the complete updated location bravo!
        }
    }

    /**
     * Swaps the worker placed in cell1 with that in cell2
     * @param cell1
     * @param cell2
     */
    public void swapLocation(Cell cell1, Cell cell2) {
        Worker worker1 = getOccupant(cell1);
        Worker worker2 = getOccupant(cell2);
        this.map.put(cell1, worker2);
        this.map.put(cell2, worker1);

        this.update();  //every time i change the location I send a copy of the complete updated location
    }

    /**
     * Method to get the cell where a worker is
     * @param worker
     * @return {@link Cell} where the worker is
     */
    public Cell getLocation(Worker worker){
        if (worker != null && this.map.containsValue(worker)) {
            for (HashMap.Entry<Cell, Worker> entry : this.map.entrySet()) {
                if (worker.equals(entry.getValue())) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    /**
     * Method to get which worker is in a cell
     * @param cell
     * @return Worker in that cell
     */
    public  Worker getOccupant(Cell cell) {
        return this.map.get(cell);
    }


    /**
     * Method to remove a worker from his cell
     * @param worker
     */
    public void removeLocation(Worker worker){
        Cell cell = getLocation(worker);
        map.put(cell, null);
    }


    private void update (){
        publish(new Message("ALL", TypeOfMessage.LOCATION_UPDATED, this));
    }


}




