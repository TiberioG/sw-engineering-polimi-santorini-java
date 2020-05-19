package it.polimi.ingsw.psp40.view.gui;

import it.polimi.ingsw.psp40.commons.Colors;
import it.polimi.ingsw.psp40.commons.messages.Message;
import it.polimi.ingsw.psp40.commons.messages.TypeOfMessage;
import it.polimi.ingsw.psp40.model.Card;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CardScreenController2 extends ScreenController {

    public TilePane centerTile;
    public Button endButton;
    private int toSelect;
    List<Integer> positionSelectedList = new ArrayList<Integer>();





    @FXML
    public void initialize(HashMap<Integer, Card> cardsMap, int toSelect) {
        List<Card> cards = new ArrayList<>(cardsMap.values());


        //centerTile.setTileAlignment(Pos.CENTER);
        centerTile.setPrefColumns(1);
        centerTile.setPrefTileWidth(120);


        cards.forEach( card -> {
          ImageView cardView =  new ImageView( new Image(GUIProperties.class.getResource("/cards/" + card.getId() +".png").toString()) ) ;
          cardView.setFitWidth(100);
          cardView.setPreserveRatio(true);
          cardView.setSmooth(true);
           cardView.setCache(true);
           centerTile.getChildren().add(cardView);
           System.out.println(card.getId());

        });

    }




}
