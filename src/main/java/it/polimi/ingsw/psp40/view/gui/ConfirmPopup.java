package it.polimi.ingsw.psp40.view.gui;

import it.polimi.ingsw.psp40.commons.FunctionInterface;
import it.polimi.ingsw.psp40.view.cli.Utils;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ConfirmPopup extends PopupStage {
    private FunctionInterface confirmFunction;
    private FunctionInterface denyFunction;
    private String copyConfirmButton;
    private String copyDenyButton;
    private String text;

    public ConfirmPopup(Stage ownerStage, String text, FunctionInterface confirmFunction, FunctionInterface denyFunction) {
        super(ownerStage);
        this.confirmFunction = confirmFunction;
        this.denyFunction = denyFunction;
        this.text  = text;
        UtilsGUI.addClassToElement(vBox, "vbox-popup-stage"); //default class
        createText();
        createTwoButtons();
    }

    public ConfirmPopup(Stage ownerStage, String text, FunctionInterface confirmFunction) {
        super(ownerStage);
        this.confirmFunction = confirmFunction;
        this.text  = text;
        UtilsGUI.addClassToElement(vBox, "vbox-popup-stage"); //default class
        createText();
        createOneButton();
    }

    public void setClass(String nameClass) {
        UtilsGUI.addClassToElement(vBox, nameClass);
    }

    public void setCopyConfirmButton(String copy) {
        copyConfirmButton = copy;
    }

    public void setCopyDenyButton(String copy) {
        copyDenyButton = copy;
    }

    private void createText() {
        this.vBox.getChildren().add(createText(text));
    }

    private void removeEffect() {
        this.ownerStage.getScene().getRoot().setEffect(null);
        this.ownerStage.getScene().getRoot().setDisable(false);
    }

    private void createOneButton() {
        Button confirmButton = createButton("Yes");
        confirmButton.setOnAction(actionEvent -> {
            removeEffect();
            this.confirmFunction.executeFunction();
        });
        this.vBox.getChildren().add(confirmButton);
    }

    private void createTwoButtons() {

        Button confirmButton = createButton("YES");
        confirmButton.setPrefHeight(35);
        confirmButton.setPrefWidth(80);
        confirmButton.setOnAction(actionEvent -> {
            removeEffect();
            this.confirmFunction.executeFunction();
        });

        Button denyButton = createButton("NO");
        denyButton.setPrefHeight(35);
        denyButton.setPrefWidth(80);
        denyButton.setOnAction(actionEvent -> {
            removeEffect();
            this.denyFunction.executeFunction();
        });

        HBox hbox = new HBox(confirmButton, denyButton);
        hbox.setSpacing(20);
        hbox.setAlignment(Pos.CENTER);
        this.vBox.getChildren().add(hbox);
    }

    private Button createButton(String label) {
        Button button = new Button(label);
        button.setPrefHeight(35);
        button.setPrefWidth(80);
        button.setStyle("-fx-font-size:18");
        UtilsGUI.buttonHoverEffect(button);
        return button;
    }
}
