package it.polimi.ingsw.commons.messages;

public class TuplaGenerics<F,S> {

    public TuplaGenerics(F first, S second){
        this.first = first;
        this.second = second;
    }

    private F first;
    private S second;


    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }
}
