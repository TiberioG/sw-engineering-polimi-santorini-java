package it.polimi.ingsw.psp40.view.gui;

import it.polimi.ingsw.psp40.commons.messages.Message;
import it.polimi.ingsw.psp40.commons.messages.TypeOfMessage;
import it.polimi.ingsw.psp40.model.Card;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CardScreenController extends ScreenController {


    HashMap<Integer, Card> cards;
    private int toSelect;
    List<Integer> positionSelectedList = new ArrayList<Integer>();
    private  int current = 0;

    @FXML
    public ImageView cardArea;

    @FXML
    public ImageView selection;

    @FXML
    private Button nextButton;
    @FXML
    private Button prevButton;
    @FXML
    private Button selectButton;
    @FXML
    private Button endButton;
    @FXML
    private TextArea textDescription;



    @FXML
    public void initialize(HashMap<Integer, Card> cards, int toSelect) {
        this.cards = cards;
        this.toSelect = toSelect;
        this.textDescription.setWrapText(true);
        this.cardArea.setImage(new Image(GUIProperties.class.getResource("/cards/" + current +".png").toString()));
        this.selection.setImage(new Image(GUIProperties.class.getResource("/images/gold.png").toString()));
        this.selection.setVisible(false);
        this.textDescription.setText(cards.get(current).getDescription());
    }


    @FXML
     void loadImage(int position) {
        int id = cards.get(position).getId();
        this.cardArea.setImage(new Image(GUIProperties.class.getResource("/cards/" + id +".png").toString()));
        this.textDescription.setText(cards.get(id).getDescription());
        if (positionSelectedList.contains(id)){
            this.selection.setVisible(true);
        }
        else {
            this.selection.setVisible(false);
        }

    }


    @FXML
    void nextCard(){
        if (current < cards.size() - 1 ){
            current ++;
        }
        loadImage(current);
        System.out.println(current);
    }

    @FXML
    void prevCard(){
        if (current > 0){
            current --;
        }
        loadImage(current);
        System.out.println(current);
    }


    @FXML
    void selectCard(){
        if (positionSelectedList.contains(current)){
            System.out.println("removable: " + current + " inlist: " + positionSelectedList.size());
            for (int i = 0; i < positionSelectedList.size(); i++) {
                if (current == positionSelectedList.get(i)) {
                    positionSelectedList.remove(i);
                }
            }
            this.selection.setVisible(false);
        }
        else if (positionSelectedList.size() < toSelect){
            System.out.println("settable: " + current + " inlist: " + positionSelectedList.size());
            positionSelectedList.add(current);
            this.selection.setVisible(true);
        }
    }

    @FXML
    void end(){
        if (positionSelectedList.size() == toSelect){
            int[] ret = new int[toSelect];
            for (int i=0; i < toSelect; i++)
            {
                ret[i] = cards.get(positionSelectedList.get(i)).getId();
            }
            getClient().sendToServer(new Message( TypeOfMessage.SET_CARDS_TO_GAME, ret));
        }

    }

}
