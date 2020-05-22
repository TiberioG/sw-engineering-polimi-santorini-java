package it.polimi.ingsw.psp40.view.gui;

import it.polimi.ingsw.psp40.commons.messages.Message;
import it.polimi.ingsw.psp40.commons.messages.TypeOfMessage;
import it.polimi.ingsw.psp40.model.Card;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CardScreenControllerPersonal extends ScreenController {
    private boolean waiting = false;
    @FXML
    private HBox hbox;
    @FXML
    private Button endButton;
    @FXML
    private TextArea textDescr;
    @FXML
    private TextArea textTitle;
    @FXML
    private TextArea selected;


    List<Integer> selectedList = new ArrayList<Integer>();

    List<Card> cards;


    @FXML
    public void initialize(List<Card> cards) {
       this.cards =cards;

        List<ImageView> cardimage = new ArrayList<>();

        textDescr.setWrapText(true);
        //UtilsGUI.addClassToElement(textTitle, "card-title");

        cards.forEach( card -> {
          ImageView cardView =  new ImageView( new Image(GUIProperties.class.getResource("/cardsFrame/" + card.getId() +".png").toString()) ) ;
          cardView.setFitHeight(250);
          cardView.setPreserveRatio(true);
          cardView.setSmooth(true);
          cardView.setCache(true);
          cardView.getStyleClass().add("card-image");
          cardView.hoverProperty().addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
                if (newValue) {
                    textDescr.setText(card.getDescription());
                    textTitle.setText(card.getName());
                } else {
                    textDescr.setText("");
                    textTitle.setText("");
                }
            });

            cardView.addEventHandler(MouseEvent.MOUSE_PRESSED,
                    mouseEvent -> {
                        if (selectedList.contains(card.getId())){
                            for (int i = 0; i < selectedList.size(); i++) {
                                if (card.getId() == selectedList.get(i)) {
                                    selectedList.remove(i);
                                    ColorAdjust colorAdjust = new ColorAdjust();
                                    colorAdjust.setBrightness(0);

                                    cardView.setEffect(colorAdjust);
                                }
                            }
                        }
                        else if (selectedList.size() < 1){
                            System.out.println("settable: " + card.getId());
                            selectedList.add(card.getId());
                            ColorAdjust colorAdjust = new ColorAdjust();
                            colorAdjust.setBrightness(0.5);
                            cardView.setEffect(colorAdjust);
                        }
            });

          cardimage.add(cardView);

        });

        for (int i = 0; i< cardimage.size(); i++){
            hbox.getChildren().add(cardimage.get(i));
        }

    }


    @FXML
    void end(){
        if (selectedList.size() == 1){
                getClient().sendToServer(new Message( TypeOfMessage.SET_CARD_TO_PLAYER, selectedList.get(0)));

        }
    }


}
