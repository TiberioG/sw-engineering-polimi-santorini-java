package it.polimi.ingsw.psp40.view.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.ArrayList;
import java.util.List;

public class LobbyScreenController extends ScreenController {
    ObservableList<String> listOfPlayers = FXCollections.observableArrayList ();

    @FXML
    private Label titleLobbyLabel;

    @FXML
    private Label subTitleLobbyLabel;

    @FXML
    private ListView listViewPlayers;

    @FXML
    public void initialize() {}

    public void updateTitleLabel(String text) {
        Platform.runLater(() -> {
            titleLobbyLabel.setText(text);
        });
    }

    public void addPlayerToLobby(String nameOfPlayer) {
        Platform.runLater(() -> {
            if (!listOfPlayers.contains(nameOfPlayer)) listOfPlayers.add(nameOfPlayer);
            listViewPlayers.setItems(listOfPlayers);
            subTitleLobbyLabel.setVisible(true);
        });
    }


    public void removePlayerToLobby(String nameOfPlayer) {
        Platform.runLater(() -> {
            listOfPlayers.remove(nameOfPlayer);
            listViewPlayers.setItems(listOfPlayers);
            if (listOfPlayers.size() == 0) subTitleLobbyLabel.setVisible(false);
        });
    }

}
