package it.polimi.ingsw.commons.messages;

public class Tupla {

    public Tupla(Object first, Object second){
        this.first = first;
        this.second = second;
    }

    private Object first;
    private Object second;


    public Object getFirst() {
        return first;
    }

    public Object getSecond() {
        return second;
    }
}
