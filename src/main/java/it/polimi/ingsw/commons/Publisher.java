package it.polimi.ingsw.commons;

public interface Publisher {
    void addSubscriber (Subscriber subscriber );
    void removeSubscriber (Subscriber subscriber);
    void notify (Subscriber subscriber);

}
