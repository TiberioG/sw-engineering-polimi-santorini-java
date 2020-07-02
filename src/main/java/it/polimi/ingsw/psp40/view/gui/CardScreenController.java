package it.polimi.ingsw.psp40.view.gui;

import it.polimi.ingsw.psp40.commons.messages.Message;
import it.polimi.ingsw.psp40.commons.messages.TypeOfMessage;
import it.polimi.ingsw.psp40.model.Card;
import javafx.animation.*;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CardScreenController extends ScreenController {

    @FXML
    private GridPane grid;
    @FXML
    private Button endButton;
    @FXML
    private TextArea textDescr;
    @FXML
    private TextArea textTitle;
    @FXML
    private TextArea selex;
    @FXML
    private HBox hbox;

    private int toSelect = 9999;
    private final IntegerProperty toSelectProperty = new SimpleIntegerProperty(toSelect);
    private final ObservableList<Integer> selectedList = FXCollections.observableArrayList();
    private final HashMap<Card, ImageView> cardsMap = new HashMap<>();
    private TypeOfMessage typeOfMessageToSend;

    /**
     * Method for initialized the cardScreenController object
     */
    @FXML
    public void initialize() {
        //ScreenController.addClassToElement(textTitle, "card-title");
        textDescr.setWrapText(true);
        BooleanBinding binding = Bindings.size(selectedList).isEqualTo(toSelectProperty);
        endButton.visibleProperty().bind(binding);
        UtilsGUI.buttonHoverEffect(endButton);
    }

    /**
     * Method to manage the visualization of the available match cards in the game
     *
     * @param cards    the available cards for selection
     * @param toSelect the number of cards to select
     */
    protected void displayCardsForInitialSelection(List<Card> cards, int toSelect) {
        this.toSelect = toSelect;
        selex.setText("Choose " + toSelect + " cards");
        this.toSelectProperty.setValue(this.toSelect);
        typeOfMessageToSend = TypeOfMessage.SET_CARDS_TO_GAME;
        buildCards(cards); // build images

        // add images to GridPane
        List<ImageView> cardImages = new ArrayList<>(cardsMap.values());
        int index = 0;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                grid.add(cardImages.get(index), row, col);
                index++;
            }
        }
    }

    /**
     * Method to manage the display of the selected available cards
     *
     * @param availableCards the available cards for the personal card selection
     */
    protected void displayCardsForPersonalSelection(List<Card> availableCards) {
        selex.setText("Choose your card ");
        this.toSelect = 1;
        this.toSelectProperty.setValue(this.toSelect);
        typeOfMessageToSend = TypeOfMessage.SET_CARD_TO_PLAYER;
        selectedList.clear();
        buildCards(availableCards);

        // add images to HBox
        List<ImageView> cardImages = new ArrayList<>(cardsMap.values());
        cardImages.forEach(cardImage -> hbox.getChildren().add(cardImage));
        grid.setVisible(false);
        hbox.setVisible(true);
    }

    /**
     * creates visualization of cards
     *
     * @param cards
     */
    private void buildCards(List<Card> cards) {
        cardsMap.clear();
        cards.forEach(card -> {
            ImageView cardView = new ImageView(new Image(GUIProperties.class.getResource("/images/cardsFrame/" + card.getId() + ".png").toString()));
            cardView.setFitHeight(230);
            cardView.setPreserveRatio(true);
            cardView.setSmooth(true);
            cardView.setCache(true);
            cardView.getStyleClass().add("card-image");
            Transition transition = buildTransition(cardView);
            cardView.hoverProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    textDescr.setText(card.getDescription());
                    textTitle.setText(card.getName());
                    transition.play();
                } else {
                    textDescr.setText("");
                    textTitle.setText("");

                    transition.stop();
                    cardView.scaleXProperty().setValue(1);
                    cardView.scaleYProperty().setValue(1);
                    cardView.scaleZProperty().setValue(1);
                    cardView.translateYProperty().setValue(0);
                }
            });

            cardsMap.put(card, cardView);
            //System.out.println(card.getId());
        });

        addHandlerToCards();
    }


    /**
     * Method build the transition animation of a specified node
     *
     * @param node the node to apply the animation
     * @return the builded transition
     */
    private Transition buildTransition(Node node) {
        Interpolator interpolator = Interpolator.SPLINE(0.25, 0.1, 0.25, 1);
        Duration duration = Duration.millis(500);

        ScaleTransition st = new ScaleTransition(duration, node);
        st.setInterpolator(interpolator);
        st.setToX(1.1);
        st.setToY(1.1);

        TranslateTransition tt = new TranslateTransition(duration, node);
        tt.setInterpolator(interpolator);
        tt.setToY(-20);

        ParallelTransition p = new ParallelTransition(st, tt);
        return p;
    }

    /**
     * Method to add an mouse event handler for each card contained in the map
     */
    private void addHandlerToCards() {
        cardsMap.keySet().forEach(card -> {
            ImageView cardView = cardsMap.get(card);
            cardView.addEventHandler(MouseEvent.MOUSE_PRESSED,
                    mouseEvent -> {
                        if (selectedList.contains(card.getId())) {
                            for (int i = 0; i < selectedList.size(); i++) {
                                if (card.getId() == selectedList.get(i)) {
                                    selectedList.remove(i);
                                    cardView.setEffect(null);
                                }
                            }
                        } else if (selectedList.size() < toSelect) {
                            //System.out.println("settable: " + card.getId());
                            selectedList.add(card.getId());
                            ColorAdjust colorAdjust = new ColorAdjust();
                            colorAdjust.setBrightness(0.5);
                            cardView.setEffect(colorAdjust);
                        }
                    });
        });
    }


    /**
     * sends selection
     */
    @FXML
    void end() {
        List<Integer> selectedListTmp = new ArrayList<>(selectedList);
        if (selectedListTmp.size() == toSelect) {
            Message message = new Message(typeOfMessageToSend);

            if (typeOfMessageToSend == TypeOfMessage.SET_CARDS_TO_GAME)
                message.setPayload(selectedListTmp);
            else if (typeOfMessageToSend == TypeOfMessage.SET_CARD_TO_PLAYER)
                message.setPayload(selectedListTmp.get(0));

            getClient().sendToServer(message);

            WaitingPopup popup = new WaitingPopup(getPrimaryStage(), "Waiting for the other players");
            GUI.showPopup(popup, 2);
        }

    }

}
