package it.polimi.ingsw.psp40.view.gui;

import it.polimi.ingsw.psp40.commons.FunctionInterface;
import it.polimi.ingsw.psp40.commons.messages.Message;
import it.polimi.ingsw.psp40.commons.messages.TypeOfMessage;
import it.polimi.ingsw.psp40.model.Card;
import it.polimi.ingsw.psp40.model.Cell;
import it.polimi.ingsw.psp40.model.Player;
import it.polimi.ingsw.psp40.network.client.Client;
import it.polimi.ingsw.psp40.view.ViewInterface;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class GUI extends Application implements ViewInterface {

    /* Attributes */

    private boolean mockingConnection = false;
    private boolean mockingCard = false;

    private Stage primaryStage;

    private static PopupStage popup;

    private Client client;

    private final String errorString = "ERROR";

    private static final Logger LOGGER = Logger.getLogger("GUI");

    private SetupScreenController setupScreenController;

    private LobbyScreenController lobbyScreenController;

    protected static GameScreenController gameScreenController = null;

    private CardScreenController cardScreenController;

    private PlayerScreenController playerScreenController;

    private FXMLLoader fxmlLoader;

    /* Methods */

    @Override
    public void start(Stage primaryStage) {
        Font.loadFont(getClass().getResourceAsStream("/fonts/InkBlossoms.ttf"), 28);
        Font.loadFont(getClass().getResourceAsStream("/fonts/Garamond.ttf"), 28);
        Font.loadFont(getClass().getResourceAsStream("/fonts/negotiatefree.ttf"), 28);
        this.primaryStage = primaryStage;

        primaryStage.setOnCloseRequest((WindowEvent t) -> {
            Platform.exit();
            System.exit(0);
        });

        // List<String> args = getParameters().getRaw();

        client = new Client();
        client.setView(this);

        displaySetup();
    }

    private void createMainScene(String pathOfFxmlFile, FunctionInterface functionInterface) {
        Platform.runLater(() -> {
            fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource(pathOfFxmlFile));
            Scene scene;
            try {
                scene = new Scene(fxmlLoader.load());
            } catch (IOException e) {
                e.printStackTrace();
                scene = new Scene(new Label(errorString));
            }
            primaryStage.setScene(scene);
            GUI.deletePopup();
            functionInterface.executeFunction();
        });
    }

    private String getTextForRemainingPlayers(Integer remainingPlayers) {
        String text;
        switch (remainingPlayers) {
            case 0:
                text = "The game's starting!";
                break;
            case 1:
                text = "Waiting for another player";
                break;
            default:
                text = "Waiting for others " + remainingPlayers + " players";
                break;
        }
        return text;
    }

    @Override
    public void displaySetup() {
        createMainScene("/FXML/SetupScreen.fxml", () -> {
            primaryStage.setTitle("Santorini");
            primaryStage.setResizable(false);
            primaryStage.show();
            setupScreenController = fxmlLoader.getController();
            setupScreenController.setClient(client);

            // just for testing
            if (mockingConnection) {
                setupScreenController.mockSendConnect();
            }
        });
    }

    @Override
    public void displaySetInitialPosition(List<Player> playerList) {
        Platform.runLater(() -> {
            createMainScene("/FXML/GameScreen.fxml", () -> {
                gameScreenController = fxmlLoader.getController();
                gameScreenController.setClient(client);
                gameScreenController.updateWholeIsland();
                gameScreenController.setInitialPosition(playerList);
                gameScreenController.setPlayersInfo(playerList);
            });
        });
    }

    @Override
    public void displaySetupFailure() {
        setupScreenController.errorAlert("The server is not reachable, please enter another address!");
    }

    @Override
    public void displayLogin() {
        setupScreenController.displayUserForm();

        // just for testing
        if (mockingConnection) {
            setupScreenController.mockSendLogin();
        }
    }

    @Override
    public void displayLoginSuccessful() {
        //create lobby
        System.out.println("You have been logged in successfully");
    }

    @Override
    public void displayLoginFailure(String details) {
        System.out.println(details);
        setupScreenController.errorAlert("The name is already used, enter another name!");
    }

    @Override
    public void displayUserJoined(String nameOfOPlayer, Integer remainingPlayers) {
        lobbyScreenController.updateTitleLabel(getTextForRemainingPlayers(remainingPlayers));
        lobbyScreenController.addPlayerToLobby(nameOfOPlayer);
    }

    @Override
    public void displayAddedToQueue(List<String> otherPlayer, Integer remainingPlayers) {
        createMainScene("/FXML/LobbyScreen.fxml", () -> {
            lobbyScreenController = fxmlLoader.getController();
            lobbyScreenController.setClient(client);
            lobbyScreenController.updateTitleLabel(getTextForRemainingPlayers(remainingPlayers));
            otherPlayer.forEach(player -> lobbyScreenController.addPlayerToLobby(player));
        });
    }

    @Override
    public void displayProposeRestoreMatch() {

    }

    @Override
    public void displayStartingMatch() {

    }

    @Override
    public void displayDisconnected(String details) {
        Platform.runLater(() -> {
            // Init Popup
            PopupStage popupStage = new DisconnectedPopup(primaryStage, details);
            // Show Popup
            popupStage.show();
        });
    }

    @Override
    public void displayGenericMessage(String message) {

    }

    @Override
    public void displayLocationUpdated() {
        if(gameScreenController != null) {
            Platform.runLater(()-> {
                gameScreenController.updateWorkersPosition();
            });
        }
    }

    @Override
    public void displayCellUpdated(Cell cell) {
        if(gameScreenController != null) {
            Platform.runLater(()-> {
                gameScreenController.updateCell(cell);
            });
        }
    }

    @Override
    public void displayCardSelection(HashMap<Integer, Card> cards, int numPlayers) {
        System.out.println("Card selection");

        // just for testing
        if (mockingCard) {
           int[] selection = {0, 1};
           client.sendToServer(new Message( TypeOfMessage.SET_CARDS_TO_GAME, selection));
        }
        else {
            createMainScene("/FXML/CardScreen.fxml", () -> {
                cardScreenController = fxmlLoader.getController();
                cardScreenController.setClient(client);
                cardScreenController.setPrimaryStage(primaryStage);
                cardScreenController.displayCardsForInitialSelection(new ArrayList<>(cards.values()), numPlayers);
            });
        }
    }

    @Override
    public void displayChoicePersonalCard(List<Card> availableCards) {
        System.out.println("Card personal");
        if (mockingCard) {
            int personalIdCard = availableCards.get(0).getId();
            client.sendToServer(new Message(TypeOfMessage.SET_CARD_TO_PLAYER, personalIdCard));
        }
        else {
            if(cardScreenController != null)
                cardScreenController.displayCardsForPersonalSelection(availableCards);
            else {
                createMainScene("/FXML/CardScreen.fxml", () -> {
                    cardScreenController = fxmlLoader.getController();
                    cardScreenController.setClient(client);
                    cardScreenController.setPrimaryStage(primaryStage);
                    cardScreenController.displayCardsForPersonalSelection(availableCards);
                });
            }
        }
    }

    @Override
    public void displayCardInGame(List<Card> cardInGame) {

    }

    @Override
    public void displayForcedCard(Card card) {
        System.out.println("Your card has been forced. It is: " + card.getName());
    }

    @Override
    public void displayAskFirstPlayer(List<Player> allPlayers) {
        createMainScene("/FXML/PlayerScreen.fxml", () -> {
            playerScreenController = fxmlLoader.getController();
            playerScreenController.setClient(client);
            playerScreenController.setPrimaryStage(primaryStage);
            playerScreenController.displayPlayersForInitialSelection(allPlayers);
        });

    }

    @Override
    public void displayChoiceOfAvailablePhases() {
        Platform.runLater(()-> {
            gameScreenController.askDesiredPhase();
        });
    }

    @Override
    public void displayChoiceOfAvailableCellForMove() {
        Platform.runLater(()-> {
            gameScreenController.highlightAvailableCellsForMove();
        });
    }

    @Override
    public void displayChoiceSelectionOfWorker() {
        // nothing here
    }

    @Override
    public void displayChoiceOfAvailableCellForBuild() {
        Platform.runLater(()-> {
            gameScreenController.highlightAvailableCellsForBuild();
        });
    }

    @Override
    public void displayEndTurn() {
        Platform.runLater(()-> {
            gameScreenController.endTurn();
        });
    }

    @Override
    public void displayLobbyCreated(String playersWaiting) {
        createMainScene("/FXML/LobbyScreen.fxml", () -> {
            lobbyScreenController = fxmlLoader.getController();
            lobbyScreenController.setClient(client);
            lobbyScreenController.updateTitleLabel(getTextForRemainingPlayers(Integer.parseInt(playersWaiting)));
        });
    }

    @Override
    public void displayWinnerMessage() {
        Platform.runLater(() -> {
            WinnerLoserPopup popup = new WinnerLoserPopup(primaryStage, true);
            popup.show();
        });
    }

    @Override
    public void displayLoserMessage(Player winningPlayer) {
        Platform.runLater(()-> {
            WinnerLoserPopup popup = new WinnerLoserPopup(primaryStage, false);
            popup.setWinner(winningPlayer);
            popup.show();
        });
    }

    @Override
    public void displayLoserPlayer(Player player) {
        Platform.runLater(()-> {
            WinnerLoserPopup popup = new WinnerLoserPopup(primaryStage, false);
            popup.setLoser(player);
            popup.show();
        });
    }

    static void showPopup(PopupStage popupArg) {
        popup = popupArg;
        popup.show();
    }

    static void deletePopup() {
        if(popup != null) {
            popup.close();
            popup = null;
        }
    }
}
