package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.WorkerAlreadyPresentException;

import java.util.HashMap;

/**
 * this singleton class represents the location
 * we want to represent here the 1:1 relationship between Worker and Cell
 */

public class Location {

    private HashMap<Cell, Worker> map = new HashMap<>();

    private static Location location = null; //the only instance
    /**
     * Private constructor of the singleton
     */
    private Location(){} //defalut private constructor

    public static Location myLocation (){
        if (location == null){
            location = new Location();
        }
        return location;
    }

    /**
     * method to add a pair cell-worker in the map
     * @param cell
     * @param worker
     * @throws WorkerAlreadyPresentException
     */
    public void setLocation(Cell cell, Worker worker) throws WorkerAlreadyPresentException {
        if (this.map.get(cell) != null) {
            throw new WorkerAlreadyPresentException();
        } else {
            this.map.put(cell, worker);
        }
    }

    /**
     * method to get the cell where a worker is
     * @param worker
     * @return Cell where the worker ins
     */
    public Cell getLocation(Worker worker){
        if (map.containsValue(worker)) {
            for (HashMap.Entry<Cell, Worker> entry : map.entrySet()) {
                if (worker.equals(entry.getValue())) {
                    return entry.getKey();
                }
            }
            return null;
        }
        else  return null;
    }

    /**
     * Method to get which worker is in a cell
     * @param cell
     * @return Worker in that cell
     */
    public  Worker getOccupant(Cell cell) {
        return this.map.get(cell);
    }


}




