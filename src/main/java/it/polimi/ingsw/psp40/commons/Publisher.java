package it.polimi.ingsw.psp40.commons;

import java.util.ArrayList;
import java.util.List;

public abstract class Publisher<T> {
    transient private List<Listener<T>> listeners = new ArrayList<>();

    public void publish(T object) {
        for (Listener<T> listener : listeners) {
            listener.update(object);
        }
    }

    public void addListener(Listener<T> listener) {
        if(listener != null) {
            listeners.add(listener);
        }
    }

    public void removeListener(Listener<T> listener){
        listeners.remove(listener);
    }

}
