package it.polimi.ingsw.psp40.view.gui;

import it.polimi.ingsw.psp40.commons.FunctionInterface;
import it.polimi.ingsw.psp40.model.Player;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

;

public class WinnerLoserPopup extends PopupStage {

    private boolean isWinner;
    private Player winningPlayer;
    private FunctionInterface continueFunction;
    private String labelButton = "Restart game!";

    protected Image image_popup_winner = new Image(getClass().getResource("/images/winner_popup.png").toString());
    protected Image image_popup_loser = new Image(getClass().getResource("/images/loser_popup.png").toString());


    /**
     * Constructor
     *
     * @param ownerStage
     * @param isWinner
     * @param continueFunction
     */
    WinnerLoserPopup(Stage ownerStage, boolean isWinner, FunctionInterface continueFunction) {
        this(ownerStage, isWinner, continueFunction, null);
    }

    /**
     * Constructor
     * @param ownerStage
     * @param isWinner
     * @param continueFunction
     * @param winningPlayer
     */
    WinnerLoserPopup(Stage ownerStage, boolean isWinner, FunctionInterface continueFunction, Player winningPlayer) {
        super(ownerStage);
        this.winningPlayer = winningPlayer;
        this.isWinner = isWinner;
        this.continueFunction = continueFunction;
        build();
    }

    private void build() {
        vBox.prefHeight(171);
        vBox.prefWidth(141);

        ImageView backgroundImageView;

        if (isWinner) backgroundImageView = new ImageView(image_popup_winner);
        else backgroundImageView = new ImageView(image_popup_loser);

        backgroundImageView.setFitHeight(400);
        backgroundImageView.setPreserveRatio(true);
        pane.getChildren().add(0, backgroundImageView);

        // Create scene
        Text text = createText(getDetails());
        text.setFont(new Font(24));
        text.setWrappingWidth(280);
        this.vBox.getChildren().add(text);
        this.vBox.setPadding(new Insets(100, 20, 20, 20));  // override default padding
        UtilsGUI.addClassToElement(vBox, isWinner ? "winner-popup" : "loser-popup");

        Button button = buildButton();
        this.vBox.getChildren().add(button);
    }

    private Button buildButton() {
        Button button = new Button(labelButton);
        button.setPrefHeight(50);
        button.setPrefWidth(150);
        button.setStyle("-fx-font-size:18");
        button.setOnAction(event -> {
            this.ownerStage.getScene().getRoot().setEffect(null);
            this.ownerStage.getScene().getRoot().setDisable(false);
            this.hide();
            continueFunction.executeFunction();
        });
        UtilsGUI.buttonHoverEffect(button);
        return button;
    }


    private String getDetails() {
        String details;
        if (isWinner) {
            details = "Congratulations!\nYou Won!";
        } else {
            details = "I'm sorry, you lost :(";
            if (winningPlayer != null) details += "\nThe winner is " + winningPlayer.getName();
        }
        return details;
    }

}
