package it.polimi.ingsw.psp40.view.gui;

import it.polimi.ingsw.psp40.commons.messages.Message;
import it.polimi.ingsw.psp40.commons.messages.TypeOfMessage;
import it.polimi.ingsw.psp40.model.Card;
import it.polimi.ingsw.psp40.model.Player;
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
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerScreenController extends ScreenController {
    private boolean waiting = false;


    @FXML
    private Button endButton;
    @FXML
    private TextArea textDescr;
    @FXML
    private TextArea textTitle;

    @FXML
    private TextArea text1;
    @FXML
    private TextArea text2;
    @FXML
    private TextArea text3;

    @FXML
    private ImageView papir1;
    @FXML
    private ImageView papir2;
    @FXML
    private ImageView papir3;

    private ArrayList<TextArea> playerNamesFields = new ArrayList<>();
    private ArrayList<ImageView> papiri = new ArrayList<>();

    private String playerSelected = null;

    @FXML
    public void initialize() {
        textDescr.setWrapText(true);
        papir1.setVisible(false);
        papir2.setVisible(false);
        papir3.setVisible(false);

    }

    protected void displayPlayersForInitialSelection(List<Player> allPlayers) {
        if (allPlayers.size() == 2){
            playerNamesFields.add(text1);
            playerNamesFields.add(text3);
            papiri.add(papir1);
            papiri.add(papir3);
            text1.setText(allPlayers.get(0).getName());
            text3.setText(allPlayers.get(1).getName());
        }
        else if (allPlayers.size() == 3){
            playerNamesFields.add(text1);
            playerNamesFields.add(text2);
            playerNamesFields.add(text3);
            papiri.add(papir1);
            papiri.add(papir2);
            papiri.add(papir3);
            text1.setText(allPlayers.get(0).getName());
            text2.setText(allPlayers.get(1).getName());
            text3.setText(allPlayers.get(2).getName());
        }


        for (int i = 0; i< papiri.size(); i ++){
            papiri.get(i).setVisible(true);
            Transition transition = buildTransition(papiri.get(i));
            Transition transition2 = buildTransition(playerNamesFields.get(i));

            int finalI = i;
            papiri.get(i).hoverProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    textDescr.setText(allPlayers.get(1).getCurrentCard().getDescription());
                    textTitle.setText(allPlayers.get(1).getCurrentCard().getName());
                    transition.play();
                    transition2.play();
                } else {
                    textDescr.setText("");
                    textTitle.setText("");

                    transition.stop();
                    transition2.stop();
                    papiri.get(finalI).scaleXProperty().setValue(1);
                    papiri.get(finalI).scaleYProperty().setValue(1);
                    papiri.get(finalI).scaleZProperty().setValue(1);
                    papiri.get(finalI).translateYProperty().setValue(0);
                }
            });

            playerNamesFields.get(i).hoverProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    textDescr.setText(allPlayers.get(1).getCurrentCard().getDescription());
                    textTitle.setText(allPlayers.get(1).getCurrentCard().getName());
                    transition.play();
                    transition2.play();
                } else {
                    textDescr.setText("");
                    textTitle.setText("");

                    transition.stop();
                    transition2.stop();
                    playerNamesFields.get(finalI).scaleXProperty().setValue(1);
                    playerNamesFields.get(finalI).scaleYProperty().setValue(1);
                    playerNamesFields.get(finalI).scaleZProperty().setValue(1);
                    playerNamesFields.get(finalI).translateYProperty().setValue(0);
                }
            });



            papiri.get(i).addEventHandler(MouseEvent.MOUSE_PRESSED,
                    mouseEvent -> {
                        if (playerSelected == null){
                            playerSelected = playerNamesFields.get(finalI).getText();
                            ColorAdjust colorAdjust = new ColorAdjust();
                            colorAdjust.setBrightness(0.5);
                            papiri.get(finalI).setEffect(colorAdjust);
                        }
                        else if (playerSelected.equals(playerNamesFields.get(finalI).getText())){
                            playerSelected = null;
                            papiri.get(finalI).setEffect(null);
                        }
            });


            playerNamesFields.get(i).addEventHandler(MouseEvent.MOUSE_PRESSED,
                    mouseEvent -> {
                        if (playerSelected == null){
                            playerSelected = playerNamesFields.get(finalI).getText();
                            ColorAdjust colorAdjust = new ColorAdjust();
                            colorAdjust.setBrightness(0.5);
                            papiri.get(finalI).setEffect(colorAdjust);
                        }
                        else if (playerSelected.equals(playerNamesFields.get(finalI).getText())){
                            papiri.get(finalI).setEffect(null);
                        }
                    });
        }//endfor

    }



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


    @FXML
    void end() {
        if (!(playerSelected == null)) {
            getClient().sendToServer(new Message(TypeOfMessage.SET_FIRST_PLAYER, playerSelected));
        }
    }

}
