package it.polimi.ingsw.psp40.view.gui;

import it.polimi.ingsw.psp40.commons.messages.Message;
import it.polimi.ingsw.psp40.commons.messages.TypeOfMessage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;

public class LobbyScreenController extends ScreenController {
    private ObservableList<String> listOfPlayers = FXCollections.observableArrayList();
    private ConfirmPopup restoreMatchPopup;

    @FXML
    private Text titleLobbyText;

    @FXML
    private Text subTitleLobbyText;

    @FXML
    private ListView listViewPlayers;

    @FXML
    public void initialize() {
    }

    /**
     * Method for update label of titleLobbyText
     *
     * @param text the label for update titleLobbyText
     */
    public void updateTitleLabel(String text) {
        titleLobbyText.setText(text);
    }

    /**
     * Method to view add a player to the waiting player list
     *
     * @param nameOfPlayer the name of the player to add
     */
    public void addPlayerToLobby(String nameOfPlayer) {
        if (!listOfPlayers.contains(nameOfPlayer)) listOfPlayers.add(nameOfPlayer);
        listViewPlayers.setItems(listOfPlayers);
        subTitleLobbyText.setVisible(true);
    }

    /**
     * Method to view remove a player to the waiting player list
     *
     * @param nameOfPlayer the name of the player to add
     */
    public void removePlayerFromLobby(String nameOfPlayer) {
        listOfPlayers.remove(nameOfPlayer);
        listViewPlayers.setItems(listOfPlayers);
        if (listOfPlayers.size() == 0) subTitleLobbyText.setVisible(false);
    }

    /**
     * Method to send the message to the server to retrieve the match and to close the popup
     *
     * @param restoreMatch the name of the player to add
     */
    private void onActionRestoreMatchButton(Boolean restoreMatch) {
        getClient().sendToServer(new Message(TypeOfMessage.RESTORE_MATCH, restoreMatch));
        restoreMatchPopup.hide();
        GUI.deletePopup();
    }

    /**
     * Method to make the popup visible to ask if you want to retrieve the match
     */
    public void showRestoreMatchPopup() {
        restoreMatchPopup = new ConfirmPopup(getPrimaryStage(), "A game was found broken, do you want to restore it?", () -> {
            onActionRestoreMatchButton(true);
        }, () -> {
            onActionRestoreMatchButton(false);
        });
        GUI.showPopup(restoreMatchPopup, 2);
    }


}
