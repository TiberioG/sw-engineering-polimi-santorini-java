package it.polimi.ingsw.psp40.view.gui;

import it.polimi.ingsw.psp40.network.client.Client;
import javafx.collections.ObservableList;
import javafx.scene.Node;

public abstract class ScreenController {
    private Client client;

    protected void setClient(Client client) {
        this.client = client;
    }

    protected Client getClient() {
        return client;
    }

    public static void addClassToElement(Node node, String nameOfClass) {
        ObservableList<String> listOfClasses = node.getStyleClass();
        if (listOfClasses.indexOf(nameOfClass) == -1) {
            listOfClasses.add(nameOfClass);
        }
    }


    public static void removeClassToElement(Node node, String nameOfClass) {
        node.getStyleClass().remove(nameOfClass);
    }
}
