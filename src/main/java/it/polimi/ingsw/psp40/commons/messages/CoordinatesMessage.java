package it.polimi.ingsw.psp40.commons.messages;

public class CoordinatesMessage {

    public CoordinatesMessage(int x, int y) {
        this.x = x;
        this.y = y;
    }

    private int x;
    private int y;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}

