package it.polimi.ingsw.psp40.view.gui;

import it.polimi.ingsw.psp40.model.Player;;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class WinnerLoserPopup extends PopupStage {

    private boolean isWinner;
    private Player winner;
    private Player loser;
    private boolean shouldContinue;
    private String details;

    protected Image image_popup_winner = new Image(getClass().getResource("/images/endgame_victorywin.png").toString());
    protected Image image_popup_loser = new Image(getClass().getResource("/images/endgame_windowdefeat.png").toString());
    protected Image image_popup_trumpets_left = new Image(getClass().getResource("/images/endgame_victorytrumpets_rotate.png").toString());
    protected Image image_popup_trumpets_right = new Image(getClass().getResource("/images/endgame_victorytrumpets.png").toString());
    protected Image image_popup_loser_trumpets_left = new Image(getClass().getResource("/images/endgame_defeattrumpetleft.png").toString());
    protected Image image_popup_loser_trumpets_right = new Image(getClass().getResource("/images/endgame_defeattrumpetright.png").toString());


    WinnerLoserPopup(Stage ownerStage, boolean isWinner) {
        super(ownerStage);
        this.isWinner = isWinner;
        build();
    }

    WinnerLoserPopup(Stage ownerStage, boolean isWinner, String details) {
        this(ownerStage, isWinner);
        this.details = details;
        build();
    }

    protected void setWinner(Player winningPlayer) {
        this.winner = winningPlayer;
    }

    protected void setLoser(Player losingPlayer) {
        this.loser = losingPlayer;
        this.shouldContinue = true;
    }

    private void build() {
        vBox.setLayoutX(225);
        vBox.setLayoutY(252);
        vBox.prefHeight(171);
        vBox.prefWidth(141);

        ImageView backgroundImageView;

        if (isWinner) backgroundImageView = new ImageView(image_popup_winner);
        else backgroundImageView = new ImageView(image_popup_loser);

        backgroundImageView.setFitHeight(422);
        backgroundImageView.setFitWidth(418);
        backgroundImageView.setLayoutX(92);
        backgroundImageView.setLayoutY(65);
        backgroundImageView.setPickOnBounds(true);
        backgroundImageView.setPreserveRatio(true);
        pane.getChildren().add(0, backgroundImageView);

        ImageView imageTrumpetsLeft;
        ImageView imageTrumpetsRight;
        if (isWinner) {
            imageTrumpetsLeft = new ImageView(image_popup_trumpets_left);
            imageTrumpetsRight = new ImageView(image_popup_trumpets_right);
        } else {
            imageTrumpetsLeft = new ImageView(image_popup_loser_trumpets_left);
            imageTrumpetsRight = new ImageView(image_popup_loser_trumpets_right);
        }

        imageTrumpetsLeft.setFitHeight(150);
        imageTrumpetsLeft.setFitWidth(153);
        imageTrumpetsLeft.setLayoutX(29);
        imageTrumpetsLeft.setLayoutY(156);
        imageTrumpetsLeft.setPickOnBounds(true);
        imageTrumpetsLeft.setPreserveRatio(true);
        pane.getChildren().add(0, imageTrumpetsLeft);
        imageTrumpetsRight.setFitHeight(293);
        imageTrumpetsRight.setFitWidth(135);
        imageTrumpetsRight.setLayoutX(406);
        imageTrumpetsRight.setLayoutY(156);
        imageTrumpetsRight.setPickOnBounds(true);
        imageTrumpetsRight.setPreserveRatio(true);
        pane.getChildren().add(0, imageTrumpetsRight);

        // Create scene
        Text text = createText(getDetails());
        this.vBox.getChildren().add(text);
        UtilsGUI.addClassToElement(vBox, isWinner ? "winner-popup" : "loser-popup");

        Button button = buildButton();
        this.vBox.getChildren().add(button);
    }

    private Button buildButton() {

        String label = shouldContinue ? "Continue" : "Something";

        Button button = new Button(label);
        button.setOnAction(event -> {
            if(shouldContinue) {
                this.ownerStage.getScene().getRoot().setEffect(null);
                this.ownerStage.getScene().getRoot().setDisable(false);
                this.hide();
            } else {
                // todo: back to home or close?
            }
        });
        UtilsGUI.buttonHoverEffect(button);
        return button;
    }


    private String getDetails() {
        String details;
        if(isWinner) {
            details = "Congratulations!\nYou Won!";
        } else { // someone has lost. You or someone else
            if(loser != null) {
                details = loser.getName() + " has lost";
            } else {
                details = "I'm sorry, you lost :(";
                if (winner != null) {
                    details += "\n\nThe winner is " + winner.getName();
                }
            }
        }
        return details;
    }

}
