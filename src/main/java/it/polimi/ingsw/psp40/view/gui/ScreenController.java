package it.polimi.ingsw.psp40.view.gui;

import it.polimi.ingsw.psp40.network.client.Client;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.stage.Stage;

public abstract class ScreenController {
    private Client client;
    private Stage primaryStage;

    /**
     * Set the client
     * @param client client to be setted
     */
    protected void setClient(Client client) {
        this.client = client;
    }

    /**
     * Set the stage
     * @param stage client to be setted
     */
    protected void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    /**
     * Return the client
     * @return the client
     */
    protected Client getClient() {
        return client;
    }

    /**
     * Return the primaryStage
     * @return the primaryStage
     */
    protected Stage getPrimaryStage(){
        return primaryStage;
    }
}
