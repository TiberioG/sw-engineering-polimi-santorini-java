package it.polimi.ingsw.psp40.view.gui;

import it.polimi.ingsw.psp40.model.Player;;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

    WinnerLoserPopup(Stage ownerStage, boolean isWinner) {
        super(ownerStage);
        this.isWinner = isWinner;
        build();
    }

    WinnerLoserPopup(Stage ownerStage, boolean isWinner, String details) {
        super(ownerStage);
        this.isWinner = isWinner;
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
        // Create scene
        Text text = createText(getDetails());
        this.vBox.getChildren().add(text);
        UtilsGUI.addClassToElement(vBox, getCSSClassName());

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
        return button;
    }


    private String getDetails() {
        String details = null;
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

    private String getCSSClassName() {
        if(isWinner) {
            return "winner-popup";
        } else {
            return "loser-popup";
        }
    }
}
