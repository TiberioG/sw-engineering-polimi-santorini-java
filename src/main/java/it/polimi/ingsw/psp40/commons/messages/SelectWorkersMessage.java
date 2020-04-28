package it.polimi.ingsw.psp40.commons.messages;

import it.polimi.ingsw.psp40.commons.Colors;

import java.util.List;

public class SelectWorkersMessage {
    private Colors colorOfWorkers;
    private List<CoordinatesMessage> positionOfWorkers;

    public SelectWorkersMessage(Colors colorOfWorkers, List<CoordinatesMessage> positionOfWorkers) {
        this.colorOfWorkers = colorOfWorkers;
        this.positionOfWorkers = positionOfWorkers;
    }

    public Colors getColorOfWorkers() {
        return colorOfWorkers;
    }

    public List<CoordinatesMessage> getPositionOfWorkers() {
        return positionOfWorkers;
    }
}
