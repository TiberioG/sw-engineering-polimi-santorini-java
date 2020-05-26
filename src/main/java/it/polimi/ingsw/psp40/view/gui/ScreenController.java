package it.polimi.ingsw.psp40.view.gui;

import it.polimi.ingsw.psp40.network.client.Client;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.stage.Stage;

public abstract class ScreenController {
    private Client client;
    private Stage primaryStage;

    protected void setClient(Client client) {
        this.client = client;
    }
    protected void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    protected Client getClient() {
        return client;
    }

    protected Stage getPrimaryStage(){
        return primaryStage;
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
