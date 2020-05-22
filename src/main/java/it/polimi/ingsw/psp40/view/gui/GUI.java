package it.polimi.ingsw.psp40.view.gui;

import it.polimi.ingsw.psp40.commons.FunctionInterface;
import it.polimi.ingsw.psp40.commons.messages.Message;
import it.polimi.ingsw.psp40.commons.messages.TypeOfMessage;
import it.polimi.ingsw.psp40.model.Card;
import it.polimi.ingsw.psp40.model.CardManager;
import it.polimi.ingsw.psp40.model.Cell;
import it.polimi.ingsw.psp40.model.Player;
import it.polimi.ingsw.psp40.network.client.Client;
import it.polimi.ingsw.psp40.view.ViewInterface;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class GUI extends Application implements ViewInterface {

    /* Attributes */

    private boolean mocking = true;

    private Stage primaryStage;

    private Client client;

    private final String errorString = "ERROR";

    private static final Logger LOGGER = Logger.getLogger("GUI");

    private SetupScreenController setupScreenController;

    protected static GameScreenController gameScreenController = null;

    private CardScreenController2 cardScreenController;

    private FXMLLoader fxmlLoader;

    /* Methods */

    @Override
    public void start(Stage primaryStage) {
        Font.loadFont(getClass().getResourceAsStream("/fonts/InkBlossoms.ttf"), 28);
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
            functionInterface.executeFunction();
        });
    }

    @Override
    public void displaySetup() {
        createMainScene("/FXML/SetupScreen.fxml", () -> {
            primaryStage.setTitle("Santorini");
            primaryStage.setResizable(false);
            primaryStage.show();
            setupScreenController = fxmlLoader.getController();
            setupScreenController.setClient(client);

            // todo remove me, just for testing
            if (mocking) {
                setupScreenController.mockSendConnect();
            }
        });
    }

    private void displayGame() {
        new Thread(() -> { // todo controllami. displaySetInitialPosition() potrebbe essere chiamato prima di displayGame() a causa di Platform.runLater()
            createMainScene("/FXML/GameScreen.fxml", () -> {
                gameScreenController = fxmlLoader.getController();
                gameScreenController.setClient(client);
                //gameScreenController.updateWholeIsland();
            });
        }).start();

    }

    @Override
    public void displaySetInitialPosition(List<Player> playerList) {
        new Thread(() -> {
            Platform.runLater(() -> {
                gameScreenController.setInitialPosition(playerList);
            });
        }).start();
    }

    @Override
    public void displaySetupFailure() {

    }

    @Override
    public void displayLogin() {
        setupScreenController.displayUserForm();

        // todo remove me, just for testing
        if (mocking) {
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
        setupScreenController.usernameBusy();
    }

    @Override
    public void displayUserJoined(String details) {

    }

    @Override
    public void displayAddedToQueue(String details) {

    }

    @Override
    public void displayStartingMatch() {
            displayGame();
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

        // todo remove me, just for testing
        if (mocking) {
           int[] selection = {0, 1};
           client.sendToServer(new Message( TypeOfMessage.SET_CARDS_TO_GAME, selection));
        }
        else {
            createMainScene("/FXML/CardScreen2.fxml", () -> {
                cardScreenController = fxmlLoader.getController();
                cardScreenController.setClient(client);
                cardScreenController.initialize(CardManager.initCardManager().getCardMap(), numPlayers);
            });
        }
    }

    @Override
    public void displayChoicePersonalCard(List<Card> availableCards) {
        if (mocking) {
            int personalIdCard = availableCards.get(0).getId();
            client.sendToServer(new Message(TypeOfMessage.SET_CARD_TO_PLAYER, personalIdCard));
        }
        else {
            createMainScene("/FXML/CardScreen2.fxml", () -> {
                cardScreenController = fxmlLoader.getController();
                cardScreenController.setClient(client);
                cardScreenController.initialize(CardManager.initCardManager().getCardMap(), 1);
            });
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
        String playerSelected = allPlayers.get(0).getName();
        client.sendToServer(new Message(TypeOfMessage.SET_FIRST_PLAYER, playerSelected));
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
    public void displayMoveWorker() {

    }

    @Override
    public void displayBuildBlock() {

    }

    @Override
    public void displayLobbyCreated(String playersWaiting) {

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
}
