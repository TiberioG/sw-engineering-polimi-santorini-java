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
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerScreenController extends ScreenController {

    @FXML
    private ImageView left;
    @FXML
    private ImageView center;
    @FXML
    private ImageView right;

    @FXML
    private ImageView card1;
    @FXML
    private ImageView card2;
    @FXML
    private ImageView card3;

    @FXML
    private ImageView descrLeft;

    @FXML
    private ImageView descrRight;

    @FXML
    private ImageView descrCent;

    @FXML
    private Button endButton;

    @FXML
    private TextArea textTitle;

    @FXML
    private TextArea descrL;

    @FXML
    private TextArea descrR;

    @FXML
    private TextArea descrC;



    private final StringProperty playerSelected = new SimpleStringProperty(null);

    @FXML
    public void initialize() {
        center.setVisible(false);
        descrC.setVisible(false);
        textTitle.setText("Choose first player");
        BooleanBinding binding = Bindings.isNotNull(playerSelected);
        endButton.visibleProperty().bind(binding);
        UtilsGUI.buttonHoverEffect(endButton);
    }

    protected void  displayPlayersForInitialSelection(List<Player> allPlayers) {
        if (allPlayers.size() == 2){
            displayPapiro(allPlayers.get(0), 1);
            displayPapiro(allPlayers.get(1), 3);
        }
        else if (allPlayers.size() == 3){
            displayPapiro(allPlayers.get(0), 1);
            displayPapiro(allPlayers.get(1), 2);
            displayPapiro(allPlayers.get(2), 3);
        }
    }

    private void displayPapiro(Player player, int location) {
        ImageView papiro ;
        ImageView card ;
        ImageView descr ;
        TextArea textDescr ;
        switch (location){
            case 1:
                papiro = left;
                card = card1;
                descr = descrLeft;
                textDescr = descrL;
                break;
            case 2:
                papiro = center;
                card = card3;
                descr = descrCent;
                textDescr = descrC;
                break;
            case 3:
                papiro = right;
                card = card2;
                descr = descrRight;
                textDescr = descrR;
                break;
            default:
                papiro = left;
                card = card2;
                descr = descrLeft;
                textDescr = descrL;
                break;
        }
        String username = player.getName();
        papiro.setVisible(true);
        papiro.setUserData(username);
        papiro.setFitWidth(400);
        papiro.setPreserveRatio(true);
        papiro.setSmooth(true);
        papiro.setCache(true);
        papiro.setPickOnBounds(false);
        Transition transition = buildTransition(papiro);
        ImageView finalPapiro = papiro;
        papiro.hoverProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                transition.play();
            } else {
                transition.stop();
                finalPapiro.scaleXProperty().setValue(1);
                finalPapiro.scaleYProperty().setValue(1);
            }
        });
        papiro.addEventHandler(MouseEvent.MOUSE_PRESSED,
                mouseEvent -> {
                    if (playerSelected.getValue() == null){
                        playerSelected.setValue((String) finalPapiro.getUserData());
                        ColorAdjust colorAdjust = new ColorAdjust();
                        colorAdjust.setBrightness(0.3);
                        finalPapiro.setEffect(colorAdjust);
                    }
                    else if (playerSelected.getValue().equals(finalPapiro.getUserData())) {
                        playerSelected.setValue(null);
                        finalPapiro.setEffect(null);
                    }
                });


        /* Add text over papiro */
        Label label = new Label(username);
        int fontSize = (int) (papiro.layoutBoundsProperty().getValue().getWidth() * 0.08); // calculate font size depending on the width of the papiro
        label.setFont(new Font(fontSize));
        Text text = new Text(label.getText());
        text.setFont(label.getFont());
        text.setTextAlignment(TextAlignment.CENTER);
        text.setMouseTransparent(true); // text is transparent to mouse events

        double textWidth = text.getBoundsInLocal().getWidth();
        double maxTextWidth = papiro.getBoundsInLocal().getWidth() * 0.60;
        if(textWidth > maxTextWidth) { // if username is too long, truncate it
            double estimatedWidthForChar = textWidth / text.getText().length();
            int charsToRemove = (int) ((textWidth - maxTextWidth) / estimatedWidthForChar);
            String truncatedUsername = username.substring(0, username.length() - charsToRemove - 1);
            truncatedUsername += "...";
            text.setText(truncatedUsername);
        }

        double x = papiro.getX() + papiro.getBoundsInLocal().getWidth() * 0.50 - text.getBoundsInLocal().getWidth()/2;
        double y = papiro.getY() + papiro.getBoundsInLocal().getHeight() * 0.30 + text.getBoundsInLocal().getHeight()/2;
        text.setX(x);
        text.setY(y);
        text.scaleXProperty().bind(papiro.scaleXProperty());
        text.scaleYProperty().bind(papiro.scaleYProperty());


        card.setImage(new Image(GUIProperties.class.getResource("/images/cardsFrame/" + player.getCurrentCard().getId() + ".png").toString()) );
        ImageView finalDescr = descr;
        card.hoverProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                finalDescr.setVisible(true);
                textDescr.setText(player.getCurrentCard().getDescription());
                textDescr.setVisible(true);
            } else {
                finalDescr.setVisible(false);
                textDescr.setVisible(false);
            }
        });
    }

    private Transition buildTransition(Node node) {
        Interpolator interpolator = Interpolator.SPLINE(0.25, 0.1, 0.25, 1);
        Duration duration = Duration.millis(500);

        ScaleTransition st = new ScaleTransition(duration, node);
        st.setInterpolator(interpolator);
        st.setToX(1.1);
        st.setToY(1.1);

        return new ParallelTransition(st);
    }

    @FXML
    void end() {
        if (playerSelected.getValue() != null) {
            getClient().sendToServer(new Message(TypeOfMessage.SET_FIRST_PLAYER, playerSelected.getValue()));
        }

    }

}
