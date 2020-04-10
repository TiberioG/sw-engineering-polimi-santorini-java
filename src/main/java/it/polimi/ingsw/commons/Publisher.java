package it.polimi.ingsw.commons;

import java.util.ArrayList;
import java.util.List;

public abstract class Publisher<T> {
    private List<Listener<T>> listeners = new ArrayList<>();

    public void publish(T eventType, Object object) {
        for (Listener listener : listeners) {
            listener.update(eventType, object);
        }
    }

    public void addListeners(Listener<T> listener){
        listeners.add(listener);
    }
}
