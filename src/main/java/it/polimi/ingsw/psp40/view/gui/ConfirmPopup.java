package it.polimi.ingsw.psp40.view.gui;

import it.polimi.ingsw.psp40.commons.FunctionInterface;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
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
        build();
    }

    private void build() {
        // Create scene

        this.vBox.getChildren().add(createText(text));
        UtilsGUI.addClassToElement(vBox, "winner-popup");
        AnchorPane anchorPane = new AnchorPane();

        Button confirmButton = new Button("Yes");
        confirmButton.setOnAction(actionEvent -> this.confirmFunction.executeFunction());


        Button denyButton = new Button("No");
        denyButton.setOnAction(actionEvent -> this.denyFunction.executeFunction());

        anchorPane.getChildren().add(confirmButton);
        AnchorPane.setLeftAnchor(confirmButton, 10.0);
        anchorPane.getChildren().add(denyButton);
        AnchorPane.setRightAnchor(denyButton, 10.0);

        this.vBox.getChildren().add(anchorPane);
    }
}
