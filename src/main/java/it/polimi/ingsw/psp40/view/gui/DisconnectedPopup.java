package it.polimi.ingsw.psp40.view.gui;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class DisconnectedPopup extends PopupStage {

    private String details;

    DisconnectedPopup(Stage ownerStage, String details) {
        super(ownerStage);
        UtilsGUI.addClassToElement(vBox, "vbox-popup-stage"); //default class
        this.details = details;
        build();
    }

    private void build() {
        // Create scene
        Text text = createText(details + " has left the game.\nWe can't continue this match :(");
        this.vBox.getChildren().add(text);
        UtilsGUI.addClassToElement(this.vBox, "disconnected-popup");

        Button resume = new Button("Something");
        resume.setPrefHeight(50);
        resume.setPrefWidth(120);
        resume.setStyle("-fx-font-size:16");
        resume.setOnAction(event -> {
            this.ownerStage.getScene().getRoot().setEffect(null);
            this.ownerStage.getScene().getRoot().setDisable(false);
            this.hide();
            // todo back to home or close?
        });
        UtilsGUI.buttonHoverEffect(resume);

        this.vBox.getChildren().add(resume);
    }
}
