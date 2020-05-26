package it.polimi.ingsw.psp40.view.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public class LobbyScreenController extends ScreenController {
    ObservableList<String> listOfPlayers = FXCollections.observableArrayList ();

    @FXML
    private Text titleLobbyText;

    @FXML
    private Text subTitleLobbyText;

    @FXML
    private ListView listViewPlayers;

    @FXML
    public void initialize() {}

    public void updateTitleLabel(String text) {
        Platform.runLater(() -> {
            titleLobbyText.setText(text);
        });
    }

    public void addPlayerToLobby(String nameOfPlayer) {
        Platform.runLater(() -> {
            if (!listOfPlayers.contains(nameOfPlayer)) listOfPlayers.add(nameOfPlayer);
            listViewPlayers.setItems(listOfPlayers);
            subTitleLobbyText.setVisible(true);
        });
    }


    public void removePlayerToLobby(String nameOfPlayer) {
        Platform.runLater(() -> {
            listOfPlayers.remove(nameOfPlayer);
            listViewPlayers.setItems(listOfPlayers);
            if (listOfPlayers.size() == 0) subTitleLobbyText.setVisible(false);
        });
    }

}
