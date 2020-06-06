package it.polimi.ingsw.psp40.view.gui;

import it.polimi.ingsw.psp40.commons.FunctionInterface;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ConfirmPopup extends PopupStage {
    private FunctionInterface confirmFunction;
    private FunctionInterface denyFunction;
    private String text;

    public ConfirmPopup(Stage ownerStage, String text, FunctionInterface confirmFunction, FunctionInterface denyFunction) {
        super(ownerStage);
        this.confirmFunction = confirmFunction;
        this.denyFunction = denyFunction;
        this.text  = text;
        UtilsGUI.addClassToElement(vBox, "winner-popup"); //default class
        createText();
        createTwoButtons();
    }

    public ConfirmPopup(Stage ownerStage, String text, FunctionInterface confirmFunction) {
        super(ownerStage);
        this.confirmFunction = confirmFunction;
        this.text  = text;
        UtilsGUI.addClassToElement(vBox, "winner-popup"); //default class
        createText();
        createButton();
    }

    public void setClass(String nameClass) {
        UtilsGUI.addClassToElement(vBox, nameClass);
    }

    private void createText() {
        this.vBox.getChildren().add(createText(text));
    }

    private void removeEffect() {
        this.ownerStage.getScene().getRoot().setEffect(null);
        this.ownerStage.getScene().getRoot().setDisable(false);
    }

    private void createButton() {
        Button confirmButton = new Button("Okay");
        confirmButton.setOnAction(actionEvent -> {
            removeEffect();
            this.confirmFunction.executeFunction();
        });
        this.vBox.getChildren().add(confirmButton);
    }

    private void createTwoButtons() {
        AnchorPane anchorPane = new AnchorPane();
        Button confirmButton = new Button("Yes");
        confirmButton.setOnAction(actionEvent -> {
            removeEffect();
            this.confirmFunction.executeFunction();
        });

        Button denyButton = new Button("No");
        denyButton.setOnAction(actionEvent -> {
            removeEffect();
            this.denyFunction.executeFunction();
        });

        anchorPane.getChildren().add(confirmButton);
        AnchorPane.setLeftAnchor(confirmButton, 10.0);
        anchorPane.getChildren().add(denyButton);
        AnchorPane.setRightAnchor(denyButton, 10.0);
        this.vBox.getChildren().add(anchorPane);
    }
}
