package it.polimi.ingsw.psp40.view.gui;

import it.polimi.ingsw.psp40.commons.messages.Message;
import it.polimi.ingsw.psp40.commons.messages.TypeOfMessage;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;

public class LobbyScreenController extends ScreenController {
    private ObservableList<String> listOfPlayers = FXCollections.observableArrayList ();
    private ConfirmPopup restoreMatchPopup;

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

    private void onActionRestoreMatchButton(Boolean restoreMatch) {
        getClient().sendToServer(new Message(TypeOfMessage.RESTORE_MATCH, restoreMatch));
        GUI.deletePopup();

    }

    public void showRestoreMatchPopup() {
        restoreMatchPopup = new ConfirmPopup(getPrimaryStage(), "A game was found broken, you want to restore it?", () -> {
            onActionRestoreMatchButton(true);
        }, () -> {
            onActionRestoreMatchButton(false);
        });
        GUI.showPopup(restoreMatchPopup);
    }


}
