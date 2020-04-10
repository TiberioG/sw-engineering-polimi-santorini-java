package it.polimi.ingsw.commons;

public interface Listener<T> {
    public void update(T eventType, Object object);
}
